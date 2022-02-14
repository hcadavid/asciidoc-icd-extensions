/*
 * Copyright (C) 2022 hcadavid
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package rug.icdtools.docsapiclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.config.Lookup;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


/**
 *
 * @author hcadavid
 */
public class DashboardAPIClient {
    
    private String authToken;
    private CloseableHttpClient httpClient;
    private String baseURL;

    public DashboardAPIClient(String credentials,String baseURL) throws APIAccessException {
        try {
            this.baseURL = baseURL;
            this.httpClient = HttpClients.createDefault();
            this.authToken = "Bearer " + getToken(baseURL,credentials);
        } catch (IOException ex) {
            throw new APIAccessException("Unable to use documentation pipeline API at:"+baseURL,ex);
        }
    }

    /**
     * 
     * @param baseURL
     * @param credentials
     * @return
     * @throws JsonProcessingException
     * @throws IOException 
     */
    private String getToken(String baseURL, String credentials) throws JsonProcessingException, IOException {
        HttpPost post = new HttpPost(baseURL+"/auth/login");

        ObjectMapper mapper = new ObjectMapper();

        HttpEntity stringEntity = new StringEntity(mapper.writeValueAsString(new JwtAuthenticationRequest("user", "123")), ContentType.APPLICATION_JSON);

        post.setEntity(stringEntity);

        try ( CloseableHttpClient httpClient = HttpClients.createDefault();  CloseableHttpResponse response = httpClient.execute(post)) {

            String token = EntityUtils.toString(response.getEntity());

            JsonObject jsonResp = new Gson().fromJson(token, JsonObject.class);

            return jsonResp.get("access_token").getAsString();
        }

    }
    
    /**
     * 
     * @param resource
     * @param jsonObject
     * @throws APIAccessException 
     */
    public void postResource(String resource,String jsonObject) throws APIAccessException{
        try {

            HttpPost postRequest = new HttpPost(baseURL + resource);            
            postRequest.addHeader("Authorization", authToken);
            postRequest.addHeader("Content-Type", "application/json");
            postRequest.setEntity(new StringEntity(jsonObject));
            
            HttpResponse response = httpClient.execute(postRequest);
            
            if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300 ){
                throw new APIAccessException("Failure while posting resource "+resource+". Object:"+jsonObject+". HTTP Code:"+response.getStatusLine().getStatusCode());
            }
                       
        } catch (JsonProcessingException e ) {
            throw new APIAccessException("Failure while posting resource "+resource+". Object:"+jsonObject,e);
        } catch (IOException e) {
            throw new APIAccessException("Failure while posting resource "+resource+". Object:"+jsonObject,e);
        }

    }

}
