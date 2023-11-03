```
curl -d '{"name":"Politics News Inc.", "lastExecution":"2023-11-02T05:35:00", "waitingTimeInMinutes":"5", "active":"true"}' -H "Content-Type: application/json" -X POST http://localhost:8080/report/configs
```

```
curl -d '{"name":"Fashion BR Inc.", "lastExecution":"2023-11-03T05:45:00", "waitingTimeInMinutes":5, "active":true}' -H "Content-Type: application/json" -X POST http://localhost:8080/report/configs
```

```
curl -H "Content-Type: application/json" -X GET http://localhost:8080/report/configs
```

```
curl -H "Content-Type: application/json" -X GET http://localhost:8080/report/configs/scheduled
```