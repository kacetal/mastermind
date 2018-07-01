package fr.kacetal.mastermind;

import fr.kacetal.mastermind.controller.ArraysComparator;
import fr.kacetal.mastermind.controller.functions.RechercheChallengerFunction;
import fr.kacetal.mastermind.controller.functions.RechercheDefenseFunction;
import fr.kacetal.mastermind.controller.functions.RechercheDuelFunction;
import fr.kacetal.mastermind.model.Game;
import fr.kacetal.mastermind.model.GameMode;
import fr.kacetal.mastermind.model.GameType;
import fr.kacetal.mastermind.view.GameInitDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private final GameInitDialog dialog = new GameInitDialog();
    private final String configFileName = "config.properties";
    private final String configDirectoryName = "src/main/resources";
    private Map<GameType, Map<GameMode, ? extends ArraysComparator>> typeBehaviour;
    private Map<GameMode, ArraysComparator> modeRechercheBehaviour;

    private Game game;
    private Path configPath = Paths.get(configDirectoryName, configFileName);


    private void pathResourcesConfig() {
        if (!configPath.toFile().exists()) {
            try (InputStream resInStream = getClass().getClassLoader().getResourceAsStream(configFileName)) {
                configPath = Paths.get(configFileName);
                Files.copy(resInStream, configPath);
            } catch (FileAlreadyExistsException e) {
                String str = "config.properties already existé";
                byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
                String strUTF8 = null;
                try {
                    strUTF8 = new String(strBytes, "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                System.out.println(strUTF8);
                str = "ééé";
                System.out.println(str);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static void main(String[] args) {
        new Main().play();
    }

    private void finctionsInitializateur() {

        modeRechercheBehaviour = new HashMap<>();
        typeBehaviour = new HashMap<>();

        modeRechercheBehaviour.put(GameMode.CHALLENGER, new RechercheChallengerFunction(game));
        modeRechercheBehaviour.put(GameMode.DEFENSEUR, new RechercheDefenseFunction(game));
        modeRechercheBehaviour.put(GameMode.DUEL, new RechercheDuelFunction(game));

        typeBehaviour.put(GameType.RECHERCHE, modeRechercheBehaviour);
    }

    private Game gameInitializateur() {
        pathResourcesConfig();
        Game.GameBuilder builder = new Game.GameBuilder();

        return builder.setDevMode(dialog.isDeveloppeur())
                      .setGameType(dialog.getGameType())
                      .setGameMode(dialog.getGameMode())
                      .setSecretBlockLongeur(configPath)
                      .setTryNumber(configPath)
                      .buildGame();
    }

    private void play() {
        mode:
        while (true) {
            game = gameInitializateur();
            finctionsInitializateur();
            jeu:
            while (true) {
                typeBehaviour.get(game.getGameType())
                        .get(game.getGameMode())
                        .play();
                switch (dialog.getReplayMode()) {
                    case 1:
                        continue jeu;
                    case 2:
                        continue mode;
                    case 3:
                        System.exit(0);
                }
            }
        }



    }
}