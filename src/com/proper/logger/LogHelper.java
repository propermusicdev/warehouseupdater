package com.proper.logger;

import com.proper.data.diagnostics.LogEntry;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lebel on 03/03/14.
 */
public class LogHelper {

    public void log(LogEntry entry) {
        try {
            URL url = new URL("http://192.168.10.248:9080/warehouse.support/api/v1/log/new");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(entry);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            //Serialise the returned entity
            //LogEntry newEntry = mapper.readValue(conn.getInputStream(), LogEntry.class);

            //Or Get the string returned
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "");
            }

            reader.close();
            // Append Server Response To Content String but do nothing with it - for now...
            String Content = sb.toString().trim();

            conn.disconnect();
//        } catch(MalformedURLException exc) {
//            exc.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
