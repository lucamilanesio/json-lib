/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.json;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.json.sample.ArrayBean;
import net.sf.json.sample.BeanA;
import net.sf.json.sample.BeanA1763699;
import net.sf.json.sample.BeanB1763699;
import net.sf.json.sample.BeanC;
import net.sf.json.sample.IdBean;
import net.sf.json.sample.JSONTestBean;
import net.sf.json.sample.Media;
import net.sf.json.sample.MediaBean;
import net.sf.json.sample.MediaList;
import net.sf.json.sample.MediaListBean;
import net.sf.json.sample.Player;
import net.sf.json.sample.PlayerList;
import net.sf.json.sample.UnstandardBean;
import net.sf.json.util.JSONUtils;
import net.sf.json.util.JavaIdentifierTransformer;
import net.sf.json.util.NewBeanInstanceStrategy;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TestUserSubmitted extends TestCase {
   public static void main( String[] args ) {
      junit.textui.TestRunner.run( TestUserSubmitted.class );
   }

   private JsonConfig jsonConfig;

   public TestUserSubmitted( String name ) {
      super( name );
   }

   public void testBug_1635890() throws NoSuchMethodException, IllegalAccessException,
         InvocationTargetException {
      // submited by arco.vandenheuvel[at]points[dot].com

      String TEST_JSON_STRING = "{\"rateType\":\"HOTRATE\",\"rateBreakdown\":{\"rate\":[{\"amount\":\"109.74\",\"date\":{\"month\":\"01\",\"day\":\"15\",\"year\":\"2007\"}},{\"amount\":\"109.74\",\"date\":{\"month\":\"1\",\"day\":\"16\",\"year\":\"2007\"}}]}}";

      DynaBean jsonBean = (DynaBean) JSONObject.toBean( JSONObject.fromObject( TEST_JSON_STRING ) );
      assertNotNull( jsonBean );
      assertEquals( "wrong rate Type", "HOTRATE", jsonBean.get( "rateType" ) );
      assertNotNull( "null rate breakdown", jsonBean.get( "rateBreakdown" ) );
      DynaBean jsonRateBreakdownBean = (DynaBean) jsonBean.get( "rateBreakdown" );
      assertNotNull( "null rate breakdown ", jsonRateBreakdownBean );
      Object jsonRateBean = jsonRateBreakdownBean.get( "rate" );
      assertNotNull( "null rate ", jsonRateBean );
      assertTrue( "list", jsonRateBean instanceof ArrayList );
      assertNotNull( "null rate ", jsonRateBreakdownBean.get( "rate", 0 ) );
   }

   public void testBug_1650535_builders() {
      // submitted by Paul Field <paulfield[at]users[dot]sourceforge[dot]net>

      String json = "{\"obj\":\"{}\",\"array\":\"[]\"}";
      JSONObject object = JSONObject.fromObject( json );
      assertNotNull( object );
      assertTrue( object.has( "obj" ) );
      assertTrue( object.has( "array" ) );
      Object obj = object.get( "obj" );
      assertTrue( obj instanceof String );
      Object array = object.get( "array" );
      assertTrue( array instanceof String );

      json = "{'obj':'{}','array':'[]'}";
      object = JSONObject.fromObject( json );
      assertNotNull( object );
      assertTrue( object.has( "obj" ) );
      assertTrue( object.has( "array" ) );
      obj = object.get( "obj" );
      assertTrue( obj instanceof String );
      array = object.get( "array" );
      assertTrue( array instanceof String );

      json = "[\"{}\",\"[]\"]";
      JSONArray jarray = JSONArray.fromObject( json );
      assertNotNull( jarray );
      obj = jarray.get( 0 );
      assertTrue( obj instanceof String );
      array = jarray.get( 1 );
      assertTrue( array instanceof String );

      json = "['{}','[]']";
      jarray = JSONArray.fromObject( json );
      assertNotNull( jarray );
      obj = jarray.get( 0 );
      assertTrue( obj instanceof String );
      array = jarray.get( 1 );
      assertTrue( array instanceof String );

      // submitted by Elizabeth Keogh <ekeogh[at]thoughtworks[dot]com>

      Map map = new HashMap();
      map.put( "address", "1 The flats [Upper floor]" );
      map.put( "phoneNumber", "[+44] 582 401923" );
      map.put( "info1", "[Likes coffee]" );
      map.put( "info2", "[Likes coffee] [Likes tea]" );
      map.put( "info3", "[Likes coffee [but not with sugar]]" );
      object = JSONObject.fromObject( map );
      assertNotNull( object );
      assertTrue( object.has( "address" ) );
      assertTrue( object.has( "phoneNumber" ) );
      assertTrue( object.has( "info1" ) );
      assertTrue( object.has( "info2" ) );
      assertTrue( object.has( "info3" ) );
      assertTrue( object.get( "address" ) instanceof String );
      assertTrue( object.get( "phoneNumber" ) instanceof String );
      assertTrue( object.get( "info1" ) instanceof String );
      assertTrue( object.get( "info2" ) instanceof String );
      assertTrue( object.get( "info3" ) instanceof String );
   }

/* I consider this behavior of "oh I added string but it's not really a string" very evil, as there's no way to add a String that really looks like "{}"
   public void testBug_1650535_setters() {
      JSONObject object = new JSONObject();
      object.element( "obj", "{}" );
      object.element( "notobj", "{string}" );
      object.element( "array", "[]" );
      object.element( "notarray", "[string]" );
      assertTrue( object.get( "obj" ) instanceof JSONObject );
      assertTrue( object.get( "array" ) instanceof JSONArray );
      assertTrue( object.get( "notobj" ) instanceof String );
      assertTrue( object.get( "notarray" ) instanceof String );

      object.element( "str", "json,json" );
      assertTrue( object.get( "str" ) instanceof String );
   }
*/
   public void testBug_1753528_ArrayStringLiteralToString() {
      // submited by sckimos[at]gmail[dot]com
      BeanA bean = new BeanA();
      bean.setString( "[1234]" );
      JSONObject jsonObject = JSONObject.fromObject( bean );
      assertEquals( "[1234]", jsonObject.get( "string" ) );

      bean.setString( "{'key':'1234'}" );
      jsonObject = JSONObject.fromObject( bean );
      assertEquals( "{'key':'1234'}", jsonObject.get( "string" ) );
   }

   public void testBug_1763699_toBean() {
      JSONObject json = JSONObject.fromObject( "{'bbeans':[{'str':'test'}]}" );
      BeanA1763699 bean = (BeanA1763699) JSONObject.toBean( json, BeanA1763699.class );
      assertNotNull( bean );
      BeanB1763699[] bbeans = bean.getBbeans();
      assertNotNull( bbeans );
      assertEquals( 1, bbeans.length );
      assertEquals( "test", bbeans[0].getStr() );
   }

   public void testBug_1764768_toBean() {
      JSONObject json = JSONObject.fromObject( "{'beanA':''}" );
      Map classMap = new HashMap();
      classMap.put( "beanA", BeanA.class );
      BeanC bean = (BeanC) JSONObject.toBean( json, BeanC.class, classMap );
      assertNotNull( bean );
      assertNotNull( bean.getBeanA() );
      assertEquals( new BeanA(), bean.getBeanA() );
   }

   public void testBug_1769559_array_conversion() {
      JSONObject jsonObject = new JSONObject().element( "beans", new JSONArray().element( "{}" )
            .element( "{'bool':false,'integer':216,'string':'JsOn'}" ) );
      ArrayBean bean = (ArrayBean) JSONObject.toBean( jsonObject, ArrayBean.class );
      assertNotNull( bean );
      // no error should happen here

      JSONArray jsonArray = jsonObject.getJSONArray( "beans" );
      BeanA[] beans = (BeanA[]) JSONArray.toArray( jsonArray, BeanA.class );
      assertNotNull( beans );
      assertEquals( 2, beans.length );
      assertEquals( new BeanA(), beans[0] );
      assertEquals( new BeanA( false, 216, "JsOn" ), beans[1] );
   }

   public void testBug_1769578_array_conversion() {
      JSONObject jsonObject = JSONObject.fromObject( "{'media':[{'title':'Giggles'},{'title':'Dreamland?'}]}" );
      Map classMap = new HashMap();
      classMap.put( "media", MediaBean.class );
      MediaListBean bean = (MediaListBean) JSONObject.toBean( jsonObject, MediaListBean.class,
            classMap );
      assertNotNull( bean );
      assertNotNull( bean.getMedia() );
      assertTrue( bean.getMedia()
            .getClass()
            .isArray() );
      Object[] media = (Object[]) bean.getMedia();
      assertEquals( 2, media.length );
      Object mediaItem1 = media[0];
      assertTrue( mediaItem1 instanceof MediaBean );
      assertEquals( "Giggles", ((MediaBean) mediaItem1).getTitle() );
   }

   public void testDynaBeanAttributeMap() throws NoSuchMethodException, IllegalAccessException,
         InvocationTargetException {
      // submited by arco.vandenheuvel[at]points[dot].com

      JSONObject jsonObject = JSONObject.fromObject( new JSONTestBean() );
      String jsonString = jsonObject.toString();
      DynaBean jsonBean = (DynaBean) JSONObject.toBean( JSONObject.fromObject( jsonString ) );
      assertNotNull( jsonBean );
      assertEquals( "wrong inventoryID", "", jsonBean.get( "inventoryID" ) );
   }

   public void testFR_1768960_array_conversion() {
      // 2 items
      JSONObject jsonObject = JSONObject.fromObject( "{'media2':[{'title':'Giggles'},{'title':'Dreamland?'}]}" );
      Map classMap = new HashMap();
      classMap.put( "media2", MediaBean.class );
      MediaListBean bean = (MediaListBean) JSONObject.toBean( jsonObject, MediaListBean.class,
            classMap );
      assertNotNull( bean );
      assertNotNull( bean.getMedia2() );
      List media2 = bean.getMedia2();
      assertEquals( 2, media2.size() );
      Object mediaItem1 = media2.get( 0 );
      assertTrue( mediaItem1 instanceof MediaBean );
      assertEquals( "Giggles", ((MediaBean) mediaItem1).getTitle() );

      // 1 item
      jsonObject = JSONObject.fromObject( "{'media2':[{'title':'Giggles'}]}" );
      bean = (MediaListBean) JSONObject.toBean( jsonObject, MediaListBean.class, classMap );
      assertNotNull( bean );
      assertNotNull( bean.getMedia2() );
      media2 = bean.getMedia2();
      assertEquals( 1, media2.size() );
      mediaItem1 = media2.get( 0 );
      assertTrue( mediaItem1 instanceof MediaBean );
      assertEquals( "Giggles", ((MediaBean) mediaItem1).getTitle() );
   }

   public void testFR_1808430_newBeanInstance() {
      JsonConfig jsonConfig = new JsonConfig();
      jsonConfig.setNewBeanInstanceStrategy( new UnstandardBeanInstanceStrategy() );
      JSONObject jsonObject = new JSONObject();
      jsonObject.element( "id", 1 );
      jsonConfig.setRootClass( UnstandardBean.class );
      UnstandardBean bean = (UnstandardBean) JSONObject.toBean( jsonObject, jsonConfig );
      assertNotNull( bean );
      assertEquals( UnstandardBean.class, bean.getClass() );
      assertEquals( 1, bean.getId() );
   }

   public void testHandleJettisonEmptyElement() {
      JSONObject jsonObject = JSONObject.fromObject( "{'beanA':'','beanB':''}" );
      jsonConfig.setHandleJettisonEmptyElement( true );
      jsonConfig.setRootClass( BeanC.class );
      BeanC bean = (BeanC) JSONObject.toBean( jsonObject, jsonConfig );
      assertNotNull( bean );
      assertNull( bean.getBeanA() );
      assertNull( bean.getBeanB() );
   }

   public void testHandleJettisonSingleElementArray() {
      JSONObject jsonObject = JSONObject.fromObject( "{'media2':{'title':'Giggles'}}" );
      Map classMap = new HashMap();
      classMap.put( "media2", MediaBean.class );
      jsonConfig.setHandleJettisonSingleElementArray( true );
      jsonConfig.setRootClass( MediaListBean.class );
      jsonConfig.setClassMap( classMap );
      MediaListBean bean = (MediaListBean) JSONObject.toBean( jsonObject, jsonConfig );
      assertNotNull( bean );
      assertNotNull( bean.getMedia2() );
      List media2 = bean.getMedia2();
      assertEquals( 1, media2.size() );
      Object mediaItem1 = media2.get( 0 );
      assertTrue( mediaItem1 instanceof MediaBean );
      assertEquals( "Giggles", ((MediaBean) mediaItem1).getTitle() );
   }

   public void testHandleJettisonSingleElementArray2() {
      JSONObject jsonObject = JSONObject.fromObject( "{'mediaList':{'media':{'title':'Giggles'}}}" );
      Map classMap = new HashMap();
      classMap.put( "media", Media.class );
      classMap.put( "mediaList", MediaList.class );
      jsonConfig.setHandleJettisonSingleElementArray( true );
      jsonConfig.setRootClass( Player.class );
      jsonConfig.setClassMap( classMap );
      Player bean = (Player) JSONObject.toBean( jsonObject, jsonConfig );
      assertNotNull( bean );
      assertNotNull( bean.getMediaList() );
      MediaList mediaList = bean.getMediaList();
      assertNotNull( mediaList.getMedia() );
      ArrayList medias = mediaList.getMedia();
      assertEquals( "Giggles", ((Media) medias.get( 0 )).getTitle() );
   }

   public void testHandleJettisonSingleElementArray3() {
      JSONObject jsonObject = JSONObject.fromObject( "{'player':{'mediaList':{'media':{'title':'Giggles'}}}}" );
      Map classMap = new HashMap();
      classMap.put( "media", Media.class );
      classMap.put( "mediaList", MediaList.class );
      classMap.put( "player", Player.class );
      jsonConfig.setHandleJettisonSingleElementArray( true );
      jsonConfig.setRootClass( PlayerList.class );
      jsonConfig.setClassMap( classMap );
      PlayerList bean = (PlayerList) JSONObject.toBean( jsonObject, jsonConfig );
      assertNotNull( bean );
      assertNotNull( bean.getPlayer() );
      ArrayList players = bean.getPlayer();
      assertNotNull( players );
      assertNotNull( players.get( 0 ) );
      Player player = (Player) players.get( 0 );
      assertNotNull( player.getMediaList() );
      MediaList mediaList = player.getMediaList();
      assertNotNull( mediaList.getMedia() );
      ArrayList medias = mediaList.getMedia();
      assertEquals( "Giggles", ((Media) medias.get( 0 )).getTitle() );
   }

   public void testJsonWithNamespaceToDynaBean() throws Exception {
      // submited by Girish Ipadi

      jsonConfig.setJavaIdentifierTransformer( JavaIdentifierTransformer.NOOP );
      String str = "{'version':'1.0'," + "'sid':'AmazonDocStyle',    'svcVersion':'0.1',"
            + "'oid':'ItemLookup',    'params':[{            'ns:ItemLookup': {"
            + "'ns:SubscriptionId':'0525E2PQ81DD7ZTWTK82'," + "'ns:Validate':'False',"
            + "'ns:Request':{" + "'ns:ItemId':'SDGKJSHDGAJSGL'," + "'ns:IdType':'ASIN',"
            + "'ns:ResponseGroup':'Large'" + "}," + "'ns:Request':{" + "'ns:ItemId':'XXXXXXXXXX',"
            + "'ns:IdType':'ASIN'," + "'ns:ResponseGroup':'Large'" + "}" + "}" + "}]" + "} ";
      JSONObject json = JSONObject.fromObject( str, jsonConfig );
      Object bean = JSONObject.toBean( (JSONObject) json );
      assertNotNull( bean );
      List params = (List) PropertyUtils.getProperty( bean, "params" );
      DynaBean param0 = (DynaBean) params.get( 0 );
      DynaBean itemLookup = (DynaBean) param0.get( "ns:ItemLookup" );
      assertNotNull( itemLookup );
      assertEquals( "0525E2PQ81DD7ZTWTK82", itemLookup.get( "ns:SubscriptionId" ) );
   }

/* No morpher, please - Kohsuke
   public void testToBeanSimpleToComplexValueTransformation() {
      // Submitted by Oliver Zyngier
      JSONObject jsonObject = JSONObject.fromObject( "{'id':null}" );
      IdBean idBean = (IdBean) JSONObject.toBean( jsonObject, IdBean.class );
      assertNotNull( idBean );
      assertEquals( null, idBean.getId() );

      jsonObject = JSONObject.fromObject( "{'id':1}" );
      idBean = (IdBean) JSONObject.toBean( jsonObject, IdBean.class );
      assertNotNull( idBean );
      assertNotNull( idBean.getId() );
      assertEquals( 0L, idBean.getId()
            .getValue() );

      JSONUtils.getMorpherRegistry()
            .registerMorpher( new IdBean.IdMorpher(), true );
      jsonObject = JSONObject.fromObject( "{'id':1}" );
      idBean = (IdBean) JSONObject.toBean( jsonObject, IdBean.class );
      assertNotNull( idBean );
      assertEquals( new IdBean.Id( 1L ), idBean.getId() );
   }
*/
    
   protected void setUp() throws Exception {
      super.setUp();
      jsonConfig = new JsonConfig();
   }

   private static class UnstandardBeanInstanceStrategy extends NewBeanInstanceStrategy {
      public Object newInstance( Class target, JSONObject source ) throws InstantiationException,
            IllegalAccessException {
         return new UnstandardBean( source.getInt( "id" ) );
      }
   }
}