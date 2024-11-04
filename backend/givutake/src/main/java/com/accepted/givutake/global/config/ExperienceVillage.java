package com.accepted.givutake.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ExperienceVillage {

    private final Connection connection;

    @Autowired
    public ExperienceVillage(Connection connection) {
        this.connection = connection;
    }

    public void processExperienceVillageData() {
        String txtFilePath = "experience_village_data.txt";

        Resource resource = new ClassPathResource(txtFilePath);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            br.readLine(); // 첫 번째 줄(헤더)을 건너뜀

            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");

                String experienceVillageName = row[0];
                String sigunguName = row[1];
                String experienceVillageDivision = row[2].equals("NULL") ? "" : row[2];
                String experienceVillageProgram = row[3];
                String experienceVillageAddress = row[4];
                String experienceVillagePhone = row[5].equals("NULL") ? "" : row[5];
                String experienceVillageHomepageUrl = row[6].equals("NULL") ? "" : row[6];


                String regionIdxQuery = "SELECT r.region_idx FROM region r WHERE r.sigungu = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(regionIdxQuery)) {
                    pstmt.setString(1, sigunguName);
                    ResultSet rs = pstmt.executeQuery();

                    if (!rs.next()) {
                        throw new SQLException("No region_idx found for sigungu: " + sigunguName);
                    }

                    int regionIdx = rs.getInt("region_idx");

                    String insertQuery = "INSERT INTO experience_village " +
                            "(experience_village_name, region_idx, experience_village_division, " +
                            "experience_village_program, experience_village_address, " +
                            "experience_village_phone, experience_village_homepage_url) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, experienceVillageName);
                        insertStmt.setInt(2, regionIdx);
                        insertStmt.setString(3, experienceVillageDivision);
                        insertStmt.setString(4, experienceVillageProgram);
                        insertStmt.setString(5, experienceVillageAddress);
                        insertStmt.setString(6, experienceVillagePhone);
                        insertStmt.setString(7, experienceVillageHomepageUrl);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}