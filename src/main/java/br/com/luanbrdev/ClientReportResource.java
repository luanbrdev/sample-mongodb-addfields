package br.com.luanbrdev;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/report/configs")
public class ClientReportResource {
    @Inject
    ClientReportService service;

    @GET
    public List<ClientReportConfig> list() {
        return service.list();
    }

    @POST
    public List<ClientReportConfig> add(ClientReportConfig clientReportConfig) {
        service.add(clientReportConfig);
        return list();
    }
}