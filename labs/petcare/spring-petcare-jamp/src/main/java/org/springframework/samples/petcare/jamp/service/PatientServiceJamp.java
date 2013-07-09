package org.springframework.samples.petcare.jamp.service;

import io.makai.core.Export;
import io.makai.core.OnStart;
import io.makai.core.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.samples.petcare.util.EntityReference;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cmathias
 * Date: 5/29/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */


@Service("/clients/patients")
@Export
public class PatientServiceJamp {

//    private final JdbcTemplate jdbcTemplate;

//    @Inject
//    public PatientServiceJamp(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

    private Map<String, EntityReference> patients;

    //initialized similar to data loading the backend db.
    @OnStart
    private void initPatientsFromDataStorage() {
//        List<EntityReference> patientsList = jdbcTemplate.query("select p.id, p.name, (c.firstName || ' ' || c.lastName) as client from Patient p, Client c",
//                new RowMapper<EntityReference>() {
//                    public EntityReference mapRow(ResultSet rs, int row)
//                            throws SQLException {
//                        Long id = rs.getLong("ID");
//                        String label = rs.getString("NAME") + " (" + rs.getString("CLIENT") + ")";
//                        return new EntityReference(id, label);
//                    }
//                });
//
//        patients = new TreeMap<String, EntityReference>();
//        for (EntityReference entity : patientsList) {
//            patients.put(entity.getLabel(), entity);
//        }

    }

    public List<EntityReference> getPatients(String name) {
        /**
         * "select ... p.name ... from Patient p ... where lower(p.name) like ? "
         */

        List<EntityReference> references = new ArrayList<EntityReference>();
        for (String nameKey : patients.keySet()) {
            if (nameKey.toLowerCase().startsWith(name)) {//TODO: Should we be using contains?
                references.add(patients.get(name));
            }
        }
        return references;
    }

}
