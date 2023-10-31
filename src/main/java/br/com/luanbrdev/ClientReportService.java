package br.com.luanbrdev;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ClientReportService {
    @Inject
    MongoClient mongoClient;

    public List<ClientReportConfig> list() {
        List<ClientReportConfig> list = new ArrayList<>();

        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                ClientReportConfig reportConfig = new ClientReportConfig();

                reportConfig.setId(document.getString("id"));
                reportConfig.setName(document.getString("name"));
                reportConfig.setLastExecution(document.getDate("lastExecution")
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                reportConfig.setWaitingTimeInMinutes(document.getInteger("waitingTimeInMinutes"));
                reportConfig.setActive(document.getBoolean("active"));
                list.add(reportConfig);
            }
        }
        return list;
    }

    public void add(ClientReportConfig reportConfig) {
        Document document = new Document()
                .append("name", reportConfig.getName())
                .append("lastExecution", reportConfig.getLastExecution())
                .append("waitingTimeInMinutes", reportConfig.getWaitingTimeInMinutes())
                .append("active", reportConfig.isActive());

        System.out.println("Pre Insert - " + document);
        var inserted = getCollection().insertOne(document);

        System.out.println("Pos insert" + inserted);
    }

    private MongoCollection getCollection() {
        return mongoClient.getDatabase("subscribers").getCollection("clientReportCollection");
    }
}
