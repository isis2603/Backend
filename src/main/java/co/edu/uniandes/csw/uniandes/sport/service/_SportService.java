/* ========================================================================
 * Copyright 2014 uniandes
 *
 * Licensed under the MIT, The MIT License (MIT)
 * Copyright (c) 2014 uniandes

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 * ========================================================================


Source generated by CrudMaker version 1.0.0.201408112050

*/

package co.edu.uniandes.csw.uniandes.sport.service;

//import co.edu.uniandes.csw.uniandes.sport.logic.api.ISportLogicService;
import co.edu.uniandes.csw.uniandes.persistence.PersistenceManager;
import co.edu.uniandes.csw.uniandes.sport.logic.dto.SportDTO;
import co.edu.uniandes.csw.uniandes.sport.logic.dto.SportPageDTO;
import co.edu.uniandes.csw.uniandes.sport.persistence.converter.SportConverter;
import co.edu.uniandes.csw.uniandes.sport.persistence.entity.SportEntity;
import co.edu.uniandes.csw.uniandes.user.logic.dto.Login;
import co.edu.uniandes.csw.uniandes.user.logic.dto.UserDTO;
import co.edu.uniandes.csw.uniandes.user.persistence.converter.UserConverter;
import co.edu.uniandes.csw.uniandes.user.persistence.converter._UserConverter;
import co.edu.uniandes.csw.uniandes.user.persistence.entity.UserEntity;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;



public abstract class _SportService {

public static String URL_SERVICIO = System.getenv("URL1");
	
        @PersistenceContext(unitName="SportPU")
 
	protected EntityManager entityManager;
 
        @PostConstruct
        public void init() {
            try {
                entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         
        
        
        @OPTIONS
        public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
        }

        
        
        
        
	@POST
	public Response createSport(SportDTO sport) {
		if (sport != null){
                SportEntity entity = SportConverter.persistenceDTO2Entity(sport);
                JSONObject rta = new JSONObject();
                SportDTO s = new SportDTO();
                s.setName(sport.getName());
                s.setMaxAge(sport.getMaxAge());
                try {
                    entityManager.getTransaction().begin();
                    entityManager.persist(entity);
                    entityManager.getTransaction().commit();
                    rta.put("sport_id", entity.getId());
                } catch (Throwable t) {
                    t.printStackTrace();
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                s = null;
                } finally {
                        entityManager.clear();
                        entityManager.close();
                }

                return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    
                }else{
                
                return Response.status(400).build();
                
                }
                
    
        }
        
        
        @GET
        public Response getSports(@QueryParam("page") Integer page, @QueryParam("maxRecords") Integer maxRecords) {

        Query count = entityManager.createQuery("select count(u) from SportEntity u");
		Long regCount = 0L;
		regCount = Long.parseLong(count.getSingleResult().toString());
		Query q = entityManager.createQuery("select u from SportEntity u");
		if (page != null && maxRecords != null) {
		    q.setFirstResult((page-1)*maxRecords);
		    q.setMaxResults(maxRecords);
		}
		SportPageDTO response = new SportPageDTO();
		response.setTotalRecords(regCount);
		response.setRecords(SportConverter.entity2PersistenceDTOList(q.getResultList()));
        String json = new Gson().toJson(response);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(json).build();
        
    }
        
        
        //Method Options for Cors
        @OPTIONS
        @Path("{id}")
        public Response cors1(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
        }
        	
        @DELETE
	@Path("{id}")
	public Response deleteSport(@PathParam("id") Long id){
            JSONObject rta = new JSONObject();    
                try {
                    entityManager.getTransaction().begin();
                    SportEntity entity=entityManager.find(SportEntity.class, id);
                    rta.put("sport_id", entity.getId());
                    entityManager.remove(entity);
                    entityManager.getTransaction().commit();

                    
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.out.printf("Error for removed from Database....");
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
              
                } finally {
                        entityManager.clear();
                        entityManager.close();
                }
		return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
	}
        
	@GET
	@Path("{id}")
	public String getSport(@PathParam("id") Long id){
            Query q = entityManager.createQuery("select u from SportEntity u where u.id="+ id +"");
            List<SportDTO> sports = q.getResultList();
            String json = new Gson().toJson(sports);
            return json;
	}
	       
        @PUT
//        @Path("{id}")
	public Response updateSport(SportDTO sport){
		JSONObject rta = new JSONObject();
                try {
                    entityManager.getTransaction().begin();
                    SportEntity entity=entityManager.find(SportEntity.class, sport.getId());
                    entity.setMaxAge(sport.getMaxAge());
                    entity.setMinAge(sport.getMinAge());
                    entity.setName(sport.getName());
                    entityManager.getTransaction().commit();
                    System.out.printf("Sport update from Database....");
                    rta.put("sport_id",entity.getId());
                    return Response.status(200).header("Access-Control-Allow-Origin", "*").build();
                    //return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.out.printf("Error for update from Database....");
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                    //return Response.status(400).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
                     return Response.status(400).header("Access-Control-Allow-Origin", "*").build();
                } finally {
                        entityManager.clear();
                        entityManager.close();
                }
                
	}
	
       
        
        
}