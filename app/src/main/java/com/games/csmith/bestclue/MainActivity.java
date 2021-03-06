package com.games.csmith.bestclue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final int DEBUG = 1;
    private static final String GAME_KEY = "GAME_KEY";
    private static final String PREDICTIONS_KEY = "PREDICTIONS_KEY";
    private BroadcastReceiver gameStateBroadcastReceiver = new GameStateBroadcastReceiver();
    private BroadcastReceiver updatePredictionsBroadcastReceiver = new UpdatePredictionsBroadcastReceiver();
    private ViewPager viewPager;
    private BestCluePagerAdapter bestCluePagerAdapter;
    private ArrayList<Prediction> predictions = new ArrayList<>();
    private Game game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (bestCluePagerAdapter == null) {
            bestCluePagerAdapter = new BestCluePagerAdapter(getSupportFragmentManager());
        }

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(bestCluePagerAdapter);
        viewPager.setOffscreenPageLimit(7);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        if (savedInstanceState == null && game == null) {
            game = new Game(getApplicationContext());
            newGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gameStateBroadcastReceiver, new IntentFilter(Game.ACTION_GAME_STATE_CHANGED));
        registerReceiver(updatePredictionsBroadcastReceiver, new IntentFilter(Game.ACTION_UPDATE_PREDICTIONS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gameStateBroadcastReceiver);
        unregisterReceiver(updatePredictionsBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(GAME_KEY, game);
        savedInstanceState.putParcelableArrayList(PREDICTIONS_KEY, predictions);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        predictions = savedInstanceState.getParcelableArrayList(PREDICTIONS_KEY);
        viewPager.getAdapter().notifyDataSetChanged();
        game = savedInstanceState.getParcelable(GAME_KEY);
        viewPager.getAdapter().notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void newGame() {
        game.reset();
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public void onAddPlayerButtonOnClick(View view) {
        if (DEBUG == 1) {
            addPlayer("qwerty");
            addPlayer("asdf");
            game.initializePlayerPredictions();
            game.setPlayersCards(game.getPlayers().get(0), new boolean[]{
                    true,
                    true,
                    true,
                    true,
                    true,
                    false,

                    true,
                    true,
                    true,
                    true,
                    false,
                    false,

                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
            });
            game.setPlayersNumberOfCards(game.getPlayers().get(1), 9);
        } else if (DEBUG == 2) {
            addPlayer("qwerty");
            addPlayer("asdf");
            addPlayer("zxcv");
            game.initializePlayerPredictions();
            game.setPlayersCards(game.getPlayers().get(0), new boolean[]{
                    true,
                    true,
                    true,
                    true,
                    true,
                    false,

                    true,
                    false,
                    false,
                    false,
                    false,
                    false,

                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
            });
            game.setPlayersNumberOfCards(game.getPlayers().get(1), 6);
            game.setPlayersNumberOfCards(game.getPlayers().get(2), 6);
        } else {
            showAddPlayerDialog();
        }
    }

    private void showAddPlayerDialog() {
        Log.d(TAG, "showAddPlayerDialog: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_player_dialog_title)
                .setView(R.layout.dialog_enter_player_name)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText playerNameEditText = ((AlertDialog) dialog).findViewById(R.id.player_name_edit_text);
                        if (playerNameEditText != null) {
                            String playerName = playerNameEditText.getText().toString().trim();
                            addPlayer(playerName);
                        }
                    }
                });
        builder.create().show();
    }

    private void addPlayer(String playerName) {
        if (!game.containsPlayer(playerName)) {
            Player player = new Player(playerName);
            game.addPlayer(player);
            viewPager.getAdapter().notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), playerName + " is now playing.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), playerName + " is already playing!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onStartGameButtonOnClick(View view) {
        Log.d(TAG, "onStartGameButtonOnClick: ");
        game.initializePlayerPredictions();
        showSelectMainPlayerDialog();
    }

    private void showSelectMainPlayerDialog() {
        final ArrayList<Player> players = new ArrayList<>(game.getPlayers());
        CharSequence[] playersCharSequenceArray = new CharSequence[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playersCharSequenceArray[i] = players.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_main_player_dialog_title)
                .setItems(playersCharSequenceArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Player mainPlayer = players.get(which);
                        ArrayList<Player> otherPlayers = new ArrayList<>(players);
                        otherPlayers.remove(mainPlayer);
                        showSelectMainPlayersCardsDialog(mainPlayer, otherPlayers);
                    }
                });
        builder.create().show();
    }

    private void showSelectMainPlayersCardsDialog(final Player mainPlayer, final ArrayList<Player> otherPlayers) {
        final boolean[] checkedCards = new boolean[Card.getCardCount()];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_main_players_cards_dialog_title, mainPlayer.getName()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        game.setPlayersCards(mainPlayer, checkedCards);
                        Player otherPlayer = otherPlayers.get(0);
                        otherPlayers.remove(otherPlayer);
                        showSetOtherPlayersNumberOfCards(otherPlayer, otherPlayers);
                    }
                })
                .setMultiChoiceItems(Card.getCards(), checkedCards, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedCards[which] = isChecked;
                    }
                });
        builder.create().show();
    }

    private void showSetOtherPlayersNumberOfCards(final Player otherPlayer, final ArrayList<Player> otherPlayers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.set_other_players_number_of_cards_dialog_title, otherPlayer.getName()))
                .setView(R.layout.dialog_enter_player_number_of_cards)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText playerNumberOfCardsEditText = ((AlertDialog) dialog).findViewById(R.id.number_of_cards_edit_text);
                        if (playerNumberOfCardsEditText != null) {
                            String numberOfCardsText = playerNumberOfCardsEditText.getText().toString().trim();
                            int numberOfCards = Integer.parseInt(numberOfCardsText);
                            game.setPlayersNumberOfCards(otherPlayer, numberOfCards);
                            otherPlayers.remove(otherPlayer);
                            if (otherPlayers.size() > 0) {
                                Player otherPlayer = otherPlayers.get(0);
                                showSetOtherPlayersNumberOfCards(otherPlayer, otherPlayers);
                            }
                        }
                    }
                });
        builder.create().show();
    }

    public void onEndGameButtonOnClick(View view) {
        Log.d(TAG, "onEndGameButtonOnClick: ");
        showConfirmEndGameDialog();
    }

    private void showConfirmEndGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.end_game_dialog_title)
                .setMessage(R.string.end_game_dialog_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newGame();
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        builder.create().show();
    }

    public void onNewTurnButtonOnClick(View view) {
        Log.d(TAG, "onNewTurnButtonOnClick: ");
        showAskPlayerDialog();
    }

    private void showAskPlayerDialog() {
        final ArrayList<Player> players = new ArrayList<>(game.getPlayers());
        final CharSequence[] playersCharSequenceArray = new CharSequence[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playersCharSequenceArray[i] = players.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.turn_ask_player_dialog_title)
                .setItems(playersCharSequenceArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int askPlayer) {
                        showAnswerPlayerDialog(players.get(askPlayer));
                    }
                });
        builder.create().show();
    }

    private void showAnswerPlayerDialog(final Player askPlayer) {
        final ArrayList<Player> players = new ArrayList<>(game.getPlayers());
        players.remove(askPlayer);
        final CharSequence[] playersCharSequenceArray = new CharSequence[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playersCharSequenceArray[i] = players.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.turn_answer_player_dialog_title)
                .setItems(playersCharSequenceArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int answerPlayer) {
                        showAskSuspectDialog(askPlayer, players.get(answerPlayer));
                    }
                });
        builder.create().show();
    }

    private void showAskSuspectDialog(final Player askPlayer, final Player answerPlayer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.turn_suspect_dialog_title)
                .setItems(Card.getSuspects(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int suspect) {
                        showAskWeaponDialog(askPlayer, answerPlayer, new Card(Card.SUSPECT, suspect));
                    }
                });
        builder.create().show();
    }

    private void showAskWeaponDialog(final Player askPlayer, final Player answerPlayer, final Card suspect) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.turn_weapon_dialog_title)
                .setItems(Card.getWeapons(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int weapon) {
                        showAskRoomDialog(askPlayer, answerPlayer, suspect, new Card(Card.WEAPON, weapon));
                    }
                });
        builder.create().show();
    }

    private void showAskRoomDialog(final Player askPlayer, final Player answerPlayer, final Card suspect, final Card weapon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.turn_room_dialog_title)
                .setItems(Card.getRooms(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int room) {
                        showAnswerCardDialog(askPlayer, answerPlayer, suspect, weapon, new Card(Card.ROOM, room));
                    }
                });
        builder.create().show();
    }

    private void showAnswerCardDialog(final Player askPlayer, final Player answerPlayer, final Card suspect, final Card weapon, final Card room) {
        final ArrayList<Card> cardOptions = new ArrayList<>();
        cardOptions.add(suspect);
        cardOptions.add(weapon);
        cardOptions.add(room);
        cardOptions.add(new Card(Card.NONE, Card.NONE_ID));

        CharSequence[] options = new CharSequence[cardOptions.size()];
        for (int i = 0; i < options.length; i++) {
            options[i] = cardOptions.get(i).getCardName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.turn_answer_card_dialog_title)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int answerCard) {
                        final Card answer = cardOptions.get(answerCard);
                        game.handleQuestion(askPlayer, answerPlayer, suspect, weapon, room, answer);
                    }
                });
        builder.create().show();
    }

    public void onPlayerCardsTitleClick(View view) {
        bestCluePagerAdapter.handleOnPlayerCardsTitleClick(view);
    }

    public void onPlayerKnowledgeTitleClick(View view) {
        bestCluePagerAdapter.handleOnPlayerKnowledgeTitleClick(view);
    }

    private class GameStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            bestCluePagerAdapter.handleGameStateChange(game.getGameState());
        }
    }

    private class UpdatePredictionsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            bestCluePagerAdapter.updatePredictions();
        }
    }

    private class BestCluePagerAdapter extends FragmentPagerAdapter {
        BestCluePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MainFragment.newInstance(game.getGameState(), getString(R.string.main_tab_title), predictions);
            } else {
                return PlayerFragment.newInstance(game.getGameState(), game.getPlayers().get(position - 1));
            }
        }

        @Override
        public int getCount() {
            return game == null ? 1 : game.getPlayers().size() + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.main_tab_title);
            } else {
                return game.getPlayers().get(position - 1).getName();
            }
        }

        void handleGameStateChange(int gameState) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof BestClueFragment) {
                    ((BestClueFragment) fragment).setGameState(gameState);
                }
            }
        }

        private void updatePredictions() {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof BestClueFragment) {
                    ((BestClueFragment) fragment).updateKnowledge(game);
                }
            }
        }

        void handleOnPlayerCardsTitleClick(View view) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof PlayerFragment) {
                    ((PlayerFragment) fragment).handleOnPlayerCardsTitleClick(view);
                }
            }
        }

        void handleOnPlayerKnowledgeTitleClick(View view) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof PlayerFragment) {
                    ((PlayerFragment) fragment).handleOnPlayerKnowledgeTitleClick(view);
                }
            }
        }
    }
}
