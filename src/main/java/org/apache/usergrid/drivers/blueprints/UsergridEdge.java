package org.apache.usergrid.drivers.blueprints;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.apache.usergrid.java.client.Client;
import org.apache.usergrid.java.client.entities.*;

import java.util.Set;
import java.util.UUID;

/**
 * Created by ApigeeCorporation on 6/29/15.
 */
public class UsergridEdge extends Connection implements UsergridChangedThing, Edge {


  public static final String ARROW_CONNECTOR = "-->";
  public static final String COLON = ":";

  public UsergridEdge(UsergridVertex outV, UsergridVertex inV, String label, Client client) {
    String sourceID = outV.getType() + COLON + outV.getUuid();
    String targetId = inV.getType() + COLON + inV.getUuid();
    setId(sourceID, label, targetId);
    setLabel(label);
    setClientConnection(client);
  }

  public void setLabel(String label) {
    super.setLabel(label);
  }


  /**
   * sets the property id for the given connection as
   * <sourecetype:uuid>/<label>/<targettype:uuid>
   *
   * @param sourceID
   * @param label
   * @param targetId
   */
  private void setId(String sourceID, String label, String targetId) {
    assertClientInitialized();
    super.setConnectionID(sourceID + ARROW_CONNECTOR + label + ARROW_CONNECTOR + targetId);
  }

  /**
   * should return the Id for the given edge. <sourecetype:uuid>/<label>/<targettype:uuid>
   *
   * @return
   */
  public String getId() {
    /*
    1. check if client is initialized.
    2. check if the edge is valid.
    3. return the edge id.
     */
    assertClientInitialized();
    //TODO: check if edge is valid.
    return super.getPropertyId();
  }


  /**
   * Return the label associated with the edge.
   *
   * @return
   */
  public String getLabel() {

    /*
    1. get the client connection. check if its initialized.
    2. TODO: get the timestamp/label associated with the entity. // Confirm.
     */
    return super.getLabel();
  }


  /**
   * removes the edge
   */
  public void remove() {
    /*
    1. check client is initialized.
    2.check if the connection/edge is valid
    3. delete the connection . check : disconnectEntities in client.java
     */

    //TODO: validate the edge.
    Client client = getClientConnection();
    String edgeId = this.getId();
    String[] properties = edgeId.split(ARROW_CONNECTOR);
    String label = properties[1];
    String[] source = properties[0].split(COLON);
    String[] target = properties[2].split(COLON);

    UsergridVertex srcVertex = new UsergridVertex(source[0]);
    srcVertex.setUuid(UUID.fromString(source[1]));

    UsergridVertex trgVertex = new UsergridVertex(target[0]);
    trgVertex.setUuid(UUID.fromString(target[1]));
    client.disconnectEntities(srcVertex, trgVertex, label);
  }

  public void onChanged(Client client) {

  }


  /**
   * Return the tail/out or head/in vertex.
   *
   * @param direction
   * @return
   * @throws IllegalArgumentException
   */
  public Vertex getVertex(Direction direction) throws IllegalArgumentException {

    /*
    1. Check the client initialized.
    2. check the direction : IN -> connected entity , OUT -> connecting entity //TODO discuss : what if its BOTH. ?
    3. if IN :
          get the connected Entity
                (ex : c3.getEntities().get(0).getStringProperty("name"))
      else if OUT :
          get the connecting entity
                (ex : entity1.getProperties().get("metadata").get("connecting"))
      else :
          //TODO : for BOTH
     */


    String edgeId = this.getId().toString();
    String[] properties = ((String) edgeId).split(ARROW_CONNECTOR);
    String[] source = properties[0].split(COLON);
    String[] target = properties[2].split(COLON);

    switch (direction) {
      case OUT:
        UsergridVertex srcVertex = new UsergridVertex(source[0]);
        srcVertex.setUuid(UUID.fromString(source[1]));
        return srcVertex; //return source vertex
      case IN:
        UsergridVertex trgVertex = new UsergridVertex(target[0]);
        trgVertex.setUuid(UUID.fromString(target[1]));
        return trgVertex;  // return target vertex
    }

    return null;
  }

  /**
   * Not implementing for the usergrid blueprints.
   *
   * @param key
   * @param <T>
   * @return
   */
  public <T> T getProperty(String key) {
    return null;
  }

  /**
   * Not implementing for the usergrid blueprints.
   *
   * @return
   */
  public Set<String> getPropertyKeys() {
    return null;
  }

  /**
   * Not implementing for the usergrid blueprints.
   *
   * @param key
   * @param value
   */
  public void setProperty(String key, Object value) {

  }

  /**
   * Not implementing for the usergrid blueprints.
   *
   * @param key
   * @param <T>
   * @return
   */
  public <T> T removeProperty(String key) {
    return null;
  }


  protected void assertClientInitialized() {
    if (UsergridGraph.client == null) {
      //TODO: Initialize client? OR throw exception?
      throw new IllegalArgumentException("Client is not initialized");
    }
  }

}
