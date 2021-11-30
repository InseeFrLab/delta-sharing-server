package fr.insee.delta.sharing.server.configuration;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import fr.insee.delta.sharing.server.delta.DeltaShareTableLoader;
import fr.insee.delta.sharing.server.service.SharedTableManager;
import io.delta.sharing.server.config.ServerConfig;

@Configuration
public class ShareManagerConfiguration {

  Logger logger = LoggerFactory.getLogger(ShareManagerConfiguration.class);

  @Value("${fr.inseefr.lab.config.file}")
  private String configFile;

  @Bean
  public ServerConfig serverConfig() {
    // final List<ShareConfig> scl = Arrays.asList(new ShareConfig("share1", Arrays
    // .asList(new SchemaConfig("schema1", Arrays.asList(new TableConfig("table1", "s31"))))));
    // final ServerConfig sc = new ServerConfig(null, scl, null, null, "localhost", 80,
    // "/delta-sharing", 3600, 10, false, false);
    logger.info("loading from file {}", this.configFile);
    if (configFile.startsWith("classpath:")) {
      try {
        final Path path =
            Paths.get(ClassLoader.getSystemResource(configFile.replace("classpath:", "")).toURI());
        logger.info(path.toString());
        final ServerConfig sc = ServerConfig.load(path.toString());
        logger.info("loaded from file {}", this.configFile);
        return sc;
      } catch (final URISyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      final ServerConfig sc = ServerConfig.load(configFile);
      logger.info("loaded from file {}", this.configFile);
      return sc;
    }
    return null;
  }

  @Bean
  public SharedTableManager sharedTableManager() {
    return new SharedTableManager(serverConfig());
  }

  @Bean
  public DeltaShareTableLoader deltaShareTableLoader() {
    return new DeltaShareTableLoader(serverConfig());
  }

  @Bean
  ProtobufHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufHttpMessageConverter();
  }
}
