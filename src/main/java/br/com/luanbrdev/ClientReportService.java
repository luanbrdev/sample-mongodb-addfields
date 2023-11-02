package br.com.luanbrdev;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
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

    public List<ClientReportConfig> listByConfiguredTime() {
        var now = LocalDateTime.now(ZoneId.of("UTC"));
        return (List<ClientReportConfig>) getCollection().aggregate(Arrays.asList(
                new Document("$addFields", new Document("dateNow", now)),
                new Document("$addFields",
                        new Document("timeInMillisecondsAggregate", new Document("$multiply", Arrays.asList("$waitingTimeInMinutes", 60000L)))),
                new Document("$addFields", new Document("addedDateAggregate",
                        new Document("$add", Arrays.asList("$lastExecution", "$timeInMillisecondsAggregate")))),
                new Document("$addFields", new Document("shouldExecuteAggregate",
                        new Document("$and", Arrays.asList(
                                new Document("$gt", Arrays.asList("$dateNow", "$addedDateAggregate")),
                                new Document("$eq", Arrays.asList("$active", true))
                                )))),
                new Document("$match", new Document("shouldExecuteAggregate", true)))).into(new ArrayList<ClientReportConfig>());
    }

    private MongoCollection getCollection() {
        return mongoClient.getDatabase("subscribers").getCollection("clientReportCollection");
    }
}
