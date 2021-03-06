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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.ezmorph.MorphUtils;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.ezmorph.bean.MorphDynaClass;
import net.sf.ezmorph.test.ArrayAssertions;
import net.sf.json.sample.AnnotationBean;
import net.sf.json.sample.BeanA;
import net.sf.json.sample.BeanB;
import net.sf.json.sample.BeanC;
import net.sf.json.sample.BeanFoo;
import net.sf.json.sample.BeanWithFunc;
import net.sf.json.sample.ClassBean;
import net.sf.json.sample.EmptyBean;
import net.sf.json.sample.JavaIdentifierBean;
import net.sf.json.sample.EnumBean;
import net.sf.json.sample.JsonEnum;
import net.sf.json.sample.ListingBean;
import net.sf.json.sample.MappingBean;
import net.sf.json.sample.NumberBean;
import net.sf.json.sample.ObjectBean;
import net.sf.json.sample.ObjectJSONStringBean;
import net.sf.json.sample.PropertyBean;
import net.sf.json.sample.ValueBean;
import net.sf.json.util.EnumMorpher;
import net.sf.json.util.JSONTokener;
import net.sf.json.util.JSONUtils;
import net.sf.json.util.JavaIdentifierTransformer;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TestJSONObjectJdk15 extends TestCase
{
   public static void main( String[] args )
   {
      junit.textui.TestRunner.run( TestJSONObjectJdk15.class );
   }

   public TestJSONObjectJdk15( String testName )
   {
      super( testName );
   }

   public void testFromBean_AnnotationBean()
   {
      AnnotationBean bean = new AnnotationBean();
      Annotation[] annotations = bean.getClass()
            .getAnnotations();
      try{
         JSONObject.fromObject( annotations[0] );
         fail( "Expected a JSONException" );
      }
      catch( JSONException expected ){
         // ok
      }
   }

   public void testFromBean_Enum()
   {
      try{
         JSONObject.fromObject( JsonEnum.OBJECT );
         fail( "Expected a JSONException" );
      }
      catch( JSONException expected ){
         // ok
      }
   }

   public void testFromBean_EnumBean()
   {
      EnumBean bean = new EnumBean();
      bean.setJsonEnum( JsonEnum.OBJECT );
      bean.setString( "string" );
      JSONObject json = JSONObject.fromObject( bean );
      assertNotNull( json );
      assertEquals( JsonEnum.OBJECT.toString(), json.get( "jsonEnum" ) );
      assertEquals( "string", json.get( "string" ) );
   }

   public void testFromObject_AnnotationBean()
   {
      AnnotationBean bean = new AnnotationBean();
      Annotation[] annotations = bean.getClass()
            .getAnnotations();
      try{
         JSONObject.fromObject( annotations[0] );
         fail( "Expected a JSONException" );
      }
      catch( JSONException expected ){
         // ok
      }
   }

   public void testFromObject_DynaBean__Enum() throws Exception
   {
      Map properties = new HashMap();
      properties.put( "jsonEnum", JsonEnum.class );
      MorphDynaClass dynaClass = new MorphDynaClass( properties );
      MorphDynaBean dynaBean = (MorphDynaBean) dynaClass.newInstance();
      dynaBean.setDynaBeanClass( dynaClass );
      dynaBean.set( "jsonEnum", JsonEnum.OBJECT );
      JSONObject json = JSONObject.fromObject( dynaBean );
      assertNotNull( json );
      assertEquals( JsonEnum.OBJECT.toString(), json.get( "jsonEnum" ) );
   }

   public void testFromObject_Enum()
   {
      try{
         JSONObject.fromObject( JsonEnum.OBJECT );
         fail( "Expected a JSONException" );
      }
      catch( JSONException expected ){
         // ok
      }
   }

   public void testFromObject_EnumBean()
   {
      EnumBean bean = new EnumBean();
      bean.setJsonEnum( JsonEnum.OBJECT );
      bean.setString( "string" );
      JSONObject json = JSONObject.fromObject( bean );
      assertNotNull( json );
      assertEquals( JsonEnum.OBJECT.toString(), json.get( "jsonEnum" ) );
      assertEquals( "string", json.get( "string" ) );
   }

   public void testFromObject_Map__Enum()
   {
      Map properties = new HashMap();
      properties.put( "jsonEnum", JsonEnum.OBJECT );
      JSONObject json = JSONObject.fromObject( properties );
      assertNotNull( json );
      assertEquals( JsonEnum.OBJECT.toString(), json.get( "jsonEnum" ) );
   }

   public void testPut_Annotation()
   {
      AnnotationBean bean = new AnnotationBean();
      Annotation[] annotations = bean.getClass()
            .getAnnotations();
      try{
         JSONObject jsonObject = new JSONObject();
         jsonObject.element( "annotation", annotations[0] );
         fail( "Expected a JSONException" );
      }
      catch( JSONException expected ){
         // ok
      }
   }

   public void testPut_Enum()
   {
      JSONObject json = new JSONObject();
      json.element( "jsonEnum", JsonEnum.OBJECT );
      assertEquals( JsonEnum.OBJECT.toString(), json.get( "jsonEnum" ) );
   }

   public void testToBean_EnumBean()
   {
      JSONUtils.getMorpherRegistry()
            .registerMorpher( new EnumMorpher( JsonEnum.class ) );
      JSONObject json = new JSONObject();
      json.element( "jsonEnum", "OBJECT" );
      EnumBean bean = (EnumBean) JSONObject.toBean( json, EnumBean.class );
      assertNotNull( bean );
      assertEquals( bean.getJsonEnum(), JsonEnum.OBJECT );
   }
}