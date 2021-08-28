package me.khoro.main.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;


@Profile("web-file-merger")
@Service
@Slf4j
@RequiredArgsConstructor
public class WebFileMergerService {

    private final RestTemplate restTemplate;

    @SneakyThrows
    public void getAndMerge(List<String> links, Path path) {

        assert path != null : "Path can't be null";
        assert links != null : "Links list can't be null";

        log.info("Got {} links to download and merge", links.size());

        try (FileOutputStream fio = new FileOutputStream(path.toFile())) {
            int i = 0;
            for (String url : links) {
                log.debug("Processing {} url", url);

                var data = restTemplate.getForObject(url, byte[].class);
                if (data != null && data.length > 0) {
                    fio.write(data);
                    log.debug("Progress {}/{}", ++i, links.size());
                } else {
                    log.warn("Got nullable or empty data for {} url", url);
                }
            }
        }

        log.info("Completed and saved to {}", path.toFile().getAbsolutePath());
    }
}
