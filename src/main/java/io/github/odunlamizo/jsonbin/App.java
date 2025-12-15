package io.github.odunlamizo.jsonbin;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.odunlamizo.jsonbin.model.Bin;
import io.github.odunlamizo.jsonbin.model.UserList;
import io.github.odunlamizo.jsonbin.okhttp.JsonBinOkHttp;

public class App {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();

        // Get the master key from .env
        String masterKey = dotenv.get("JSONBIN_MASTER_KEY");

        // Check if the master key exists
        if (masterKey == null || masterKey.isEmpty()) {
            System.err.println("Error: JSONBIN_MASTER_KEY not found in .env file");
            System.exit(1);
        }

        JsonBin jsonBin = new JsonBinOkHttp.Builder().withMasterKey(masterKey).build();
        Bin<UserList> bin = jsonBin.readBin("687644d36063391d31ae163f", UserList.class);
        System.out.println(bin);
    }
}
