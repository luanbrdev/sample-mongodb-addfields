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
                buildResult(list, document);
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

        getCollection().insertOne(document);
    }

    public List<ClientReportConfig> listByConfiguredTime() {
        List<ClientReportConfig> list = new ArrayList<>();
        var now = LocalDateTime.now(ZoneId.of("UTC"));
        var iterator = getCollection().aggregate(Arrays.asList(
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
                new Document("$match", new Document("shouldExecuteAggregate", true)))).iterator();

        try (MongoCursor<Document> cursor = iterator) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                buildResult(list, document);
            }
        }
        return list;
    }

    private static void buildResult(List<ClientReportConfig> list, Document document) {
        ClientReportConfig reportConfig = new ClientReportConfig();

        reportConfig.setId(document.get("_id").toString());
        reportConfig.setName(document.getString("name"));
        reportConfig.setLastExecution(document.getDate("lastExecution")
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        reportConfig.setWaitingTimeInMinutes(document.getInteger("waitingTimeInMinutes"));
        reportConfig.setActive(document.getBoolean("active"));
        list.add(reportConfig);
    }

    private MongoCollection getCollection() {
        return mongoClient.getDatabase("subscribers").getCollection("clientReportCollection");
    }
}
