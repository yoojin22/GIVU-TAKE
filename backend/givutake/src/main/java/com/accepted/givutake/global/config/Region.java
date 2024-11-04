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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Region {

    private final Connection connection;

    @Autowired
    public Region(Connection connection) {
        this.connection = connection;
    }

    public void processRegionData() {
        String filePath = "region_data.txt";

        Resource resource = new ClassPathResource(filePath);

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentTime.format(formatter);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            br.readLine(); // 첫 번째 줄(헤더)을 건너뜀

            String line;
            while ((line = br.readLine()) != null) {
                String[] lineData = line.split("\t");

                if (lineData.length == 3) {
                    String fullName = lineData[1];
                    String status = lineData[2];

                    if ("존재".equals(status)) {
                        String[] parts = fullName.split(" ");

                        if (parts.length >= 1) {
                            String sido = parts[0];
                            String sigungu = parts.length > 1 ? parts[1] : null;
                            if(sigungu == null)continue;

                            String checkSql = "SELECT COUNT(*) FROM region WHERE sido = ? AND sigungu = ?";
                            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                                checkStmt.setString(1, sido);
                                checkStmt.setString(2, sigungu);
                                ResultSet rs = checkStmt.executeQuery();
                                if (rs.next() && rs.getInt(1) == 0) {
                                    String insertSql = "INSERT INTO region (sido, sigungu, created_date, modified_date) VALUES (?, ?, ?, ?)";
                                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                                        insertStmt.setString(1, sido);
                                        insertStmt.setString(2, sigungu);
                                        insertStmt.setString(3, formattedDateTime);
                                        insertStmt.setString(4, formattedDateTime);
                                        insertStmt.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}