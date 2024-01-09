import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    public static void clearDatabase() {
        // Provide the connection URI (replace "your_database" with your actual database name)
        String connectionString = "mongodb://localhost:27017/battleships";

        // Create a MongoClient using the connection URI
        MongoClientURI uri = new MongoClientURI(connectionString);
        MongoClient mongoClient = new MongoClient(uri);

        // Access the database using the database name
        MongoDatabase database = mongoClient.getDatabase(uri.getDatabase());

        // Perform database operations here...
        database.drop();

        // Close the MongoClient when done
        mongoClient.close();
    }
}
