package fr.insee.delta.sharing.server.delta;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;

public class Run {

  public static void main(String[] args) throws IOException {
    // TODO Auto-generated method stub
    final DeltaShareTable d = new DeltaShareTable(100, false,
        new Table("toto", "s3a://projet-spark-lab/diffusion/delta/test"), new Configuration());
    System.out.println(d.getTableVersion());

  }

}
