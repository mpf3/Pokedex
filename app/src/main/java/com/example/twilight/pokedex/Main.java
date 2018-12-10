package com.example.twilight.pokedex;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.NamedApiResource;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonAbility;
import me.sargunvohra.lib.pokekotlin.model.PokemonMove;
import me.sargunvohra.lib.pokekotlin.model.PokemonStat;


public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PokeApi pokeApi = new PokeApiClient();
    private Pokemon currentPokemon = null;
    private static final int MAX_POKEMON = 802;
    private Map<String, Integer> pokemonLookup = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //the app crashes when you try to use PokeKotlin without these 2 lines
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_kanto) {

        } else if (id == R.id.nav_johto) {

        } else if (id == R.id.nav_hoenn) {

        } else if (id == R.id.nav_sinnoh) {

        } else if (id == R.id.nav_unova) {

        } else if (id == R.id.nav_kalos) {

        } else if (id == R.id.nav_alola) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void apiCall(View view) {
        //initially add all pokemon to Map so you can look them up by name. only do it once
        if (pokemonLookup.isEmpty()) {
            System.out.println("I don't know how to cache please don't ban me");
            List<NamedApiResource> pokemonList = pokeApi.getPokemonSpeciesList(1, MAX_POKEMON).getResults();
            for (NamedApiResource pokemon : pokemonList) {
                pokemonLookup.put(pokemon.getName(), pokemon.getId());
            }
        }
        try {
            TextView input = findViewById(R.id.editText);
            int number = Integer.parseInt(input.getText().toString().trim());
            if (number > 0 && number < MAX_POKEMON) {
                updatePokemon(number);
            }
        } catch (Exception E) {
            //must be a string and not a number
            TextView input = findViewById(R.id.editText);
            String name = input.getText().toString().trim().replace(" ", "-").toLowerCase();
            if (pokemonLookup.containsKey(name)) {
                updatePokemon(pokemonLookup.get(name));
            } else {
                Toast.makeText(this, "Pokemon not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void next(View view) {
        if (currentPokemon != null) {
            int number = currentPokemon.getId() + 1;
            if (number > MAX_POKEMON) {
                number = 1;
            }
            updatePokemon(number);
        } else {
            updatePokemon(1);
        }
    }

    public void previous(View view) {
        if (currentPokemon != null) {
            int number = currentPokemon.getId() - 1;
            if (number <= 0) {
                number = MAX_POKEMON;
            }
            updatePokemon(number);
        } else {
            updatePokemon(MAX_POKEMON);
        }
    }

    private void updatePokemon(int num) {
        if (currentPokemon == null || currentPokemon.getId() != num) {
            currentPokemon = pokeApi.getPokemon(num);
        }

        ImageView pokemonImage = findViewById(R.id.pkmnImage);
        TextView nameBox = findViewById(R.id.pkmnName);
        TextView infoBox = findViewById(R.id.infoBox);
        TextView movesBox = findViewById(R.id.movesBox);
        TextView statsBox = findViewById(R.id.statsBox);

        Picasso.get().load(currentPokemon.getSprites().getFrontDefault()).into(pokemonImage);
        String nameString = "#" + currentPokemon.getId() + " " + currentPokemon.getName();
        nameBox.setText(nameString);
        infoBox.setText(formatInfo());
        statsBox.setText(formatStats());
        movesBox.setText(formatMoves());
    }

    private String formatInfo() {
        String infoList = "";

        //types and check if it's dual type so we don't error
        int typeAmount = currentPokemon.getTypes().size();
        String types = currentPokemon.getTypes().get(0).getType().getName();
        if (typeAmount > 1) {
            types = currentPokemon.getTypes().get(1).getType().getName() + ",-" + types;
        }
        infoList += "Type: " + makeReadable(types) + "\n";

        //abilities
        infoList += "Abilities: ";
        List<PokemonAbility> abilityList = currentPokemon.getAbilities();
        for (int i = abilityList.size() - 1; i >= 0; i--) {
            PokemonAbility ability = abilityList.get(i);
            infoList += makeReadable(ability.getAbility().getName());
            if (i != 0) {
                infoList += ", ";
            }
        }
        infoList += "\n";

        //forms
        List<NamedApiResource> forms = currentPokemon.getForms();
        if (forms.size() > 1) {
            infoList += "Forms: ";
            for (int i = forms.size() - 1; i >= 0; i--) {
                infoList += makeReadable(forms.get(i).getName());
                if (i != 0) {
                    infoList += ", ";
                }
            }
            infoList += "\n";
        }
        //height and weight, numbers are stored *10 so they don't store decimals
        infoList += "Height: " + currentPokemon.getHeight() / 10.0 + " m\n";
        infoList += "Weight: " + currentPokemon.getWeight() / 10.0 + " kg";
        return infoList;

    }

    private String formatMoves() {
        String moveString = "";
        for (PokemonMove move : currentPokemon.getMoves()) {
            moveString += makeReadable(move.getMove().getName()) + "\n";
        }
        return moveString;

    }

    private String formatStats() {
        String statString = "";
        int statTotal = 0;
        List<PokemonStat> statList = currentPokemon.getStats();
        for (int i = statList.size() - 1; i >= 0; i--) {
            PokemonStat stat = statList.get(i);
            statString += makeReadable(stat.getStat().getName() + ": " + stat.getBaseStat());
            statTotal += stat.getBaseStat();
            if (i != 0) {
                statString += "\n";
            }
        }
        statString += "\nTotal: " + statTotal;
        return statString;

    }

    private String makeReadable(String s) {
        String returnString = "";
        String[] words = s.split("-");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            returnString += word.substring(0, 1).toUpperCase() + word.substring(1);
            if (i != words.length - 1) {
                returnString += " ";
            }
        }
        return returnString;
    }
}
