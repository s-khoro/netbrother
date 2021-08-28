package me.khoro.main.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.khoro.main.model.entity.Domain;
import me.khoro.main.repository.DomainRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Reads data from file with structure
 * DOMAIN_NAME REGISTER_NAME REG_DATE LAST_UPDATE_DATE EXPIRATION_DATE
 * For example:
 * STRLED.RU	REGRU-RU	07.10.2018	07.10.2019	07.11.2019	1
 */
@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class DomainFileReader {

    private String delimeter = "\t";
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private int batchMaxSize = 1_000;

    private final DomainRepository domainRepository;

//    @PostConstruct
    private void test() {
        List<DomainInfo> domainInfos = readDomainInfo(Path.of("PUT_PATH_TO_FILE_HERE"));

        List<Domain> domains = new ArrayList<>(domainInfos.size());
        List<Domain> batch = new ArrayList<>(batchMaxSize);
        long count = 0;

        for (DomainInfo domainInfo : domainInfos) {
            Domain domain = new Domain();
            domain.setId(UUID.randomUUID());
            domain.setDomain(domainInfo.domain);
            domain.setRegDate(domainInfo.regDate);
            domain.setUpdDate(domainInfo.lastUpdateDate);
            domain.setExpDate(domainInfo.expDate);
            domain.setIsActive(domainInfo.isExpired);

            batch.add(domain);

            if (batch.size() >= batchMaxSize) {
                domainRepository.saveAll(batch);
                count += batch.size();
                batch.clear();
                log.info("[{}/{}] processed", count, domainInfos.size());
            }
        }

        if (batch.size() != 0) {
            domainRepository.saveAll(batch);
        }
    }

    public List<DomainInfo> readDomainInfo(Path path) {
        File file = path.toFile();
        if (file.exists() && file.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                List<DomainInfo> result = new ArrayList<>();
                String row;
                while ((row = br.readLine()) != null) {
                    String[] columns = row.split(delimeter);
                    try {
                        DomainInfo di = DomainInfo.builder()
                                .domain(columns[0])
                                .register(columns[1])
                                .regDate(dateFormat.parse(columns[2]))
                                .lastUpdateDate(dateFormat.parse(columns[3]))
                                .expDate(dateFormat.parse(columns[4]))
                                .isExpired(Long.parseLong(columns[5]) == 0)
                                .build();
                        result.add(di);
                    } catch (Exception e) {
                        log.info(String.format("Error during row parsing: '%s'", Arrays.toString(columns)), e);
                    }
                }
                return result;

            } catch (Exception e) {
                throw new RuntimeException("File read exception", e);
            }
        } else {
            var msg = String.format("File not found: '%s'", path.toAbsolutePath());
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }



    @Data
    @Builder
    public static class DomainInfo {
        private String domain;
        private String register;
        private Date regDate;
        private Date lastUpdateDate;
        private Date expDate;
        private boolean isExpired;
    }
}
