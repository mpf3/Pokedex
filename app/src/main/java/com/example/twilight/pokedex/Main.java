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
import android.widget.TextView;

import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonAbility;
import me.sargunvohra.lib.pokekotlin.model.PokemonMove;
import me.sargunvohra.lib.pokekotlin.model.PokemonStat;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PokeApi pokeApi = new PokeApiClient();
    private Pokemon currentPokemon = null;

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
        try {
            TextView input = findViewById(R.id.editText);
            int number = Integer.parseInt(input.getText().toString().trim());
            if (currentPokemon == null) {
                currentPokemon = pokeApi.getPokemon(number);
            } else {
                if (number != currentPokemon.getId()) {
                    currentPokemon = pokeApi.getPokemon(number);
                }
            }
            TextView nameBox = findViewById(R.id.pkmnName);
            TextView infoBox = findViewById(R.id.infoBox);
            TextView movesBox = findViewById(R.id.movesBox);
            TextView statsBox = findViewById(R.id.statsBox);

            String nameString = "#" + currentPokemon.getId() + " " + currentPokemon.getName();
            nameBox.setText(nameString);
            infoBox.setText(formatInfo());
            statsBox.setText(formatStats());
            movesBox.setText(formatMoves());

        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private String formatInfo() {
        String infoList = "";

        //types and check if it's dual type so we don't error
        int typeAmount = currentPokemon.getTypes().size();
        String types = currentPokemon.getTypes().get(0).getType().getName();
        if (typeAmount > 1) {
            types += ", " + currentPokemon.getTypes().get(1).getType().getName();
        }
        infoList += "Type: " + makeReadable(types) + "\n";

        //abilities
        infoList += "Abilities: ";
        for (PokemonAbility ability : currentPokemon.getAbilities()) {
            infoList += makeReadable(ability.getAbility().getName()) + ", ";
        }
        infoList += "\n";

        //height and weight, numbers are stored *10 so they don't store decimals
        infoList += "Height: " + currentPokemon.getHeight() / 10.0 + " m\n";
        infoList += "Weight: " + currentPokemon.getWeight() / 10.0 + " kg";
        return infoList;

    }

    private String formatMoves() {
        String moveList = "";
        for (PokemonMove move : currentPokemon.getMoves()) {
            moveList += makeReadable(move.getMove().getName()) + "\n";
        }
        return moveList;

    }

    private String formatStats() {
        String statList = "";
        for (PokemonStat stat : currentPokemon.getStats()) {
            statList += makeReadable(stat.getStat().getName()) + ": " + stat.getBaseStat() + "\n";
        }
        return statList;

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
