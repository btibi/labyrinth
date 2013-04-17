package hu.btibi.labyrinth;

import hu.btibi.labyrinth.domain.Location;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Labyrinth {

    public static void main(String[] args) throws IOException {

        LabyrinthGetter labyrinthGetter = new LabyrinthGetter();

        labyrinthGetter.getLabyrint("easy");

        labyrinthGetter.getLabyrint("pacman");

	}



}
