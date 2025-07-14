package io.github.odunlamizo.jsonbin;

import io.github.cdimascio.dotenv.Dotenv;

public class App {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();
    }
}
