/**
 * $RCSfile$
 * $Revision: 11388 $
 * $Date: 2009-11-08 18:26:55 -0600 (Sun, 08 Nov 2009) $
 *
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sbs.goodcrawler.conf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls Jive properties. Jive properties are only meant to be set and retrieved
 * by core Jive classes. Some properties may be stored in XML format while others in the
 * database.<p>
 *
 * When starting up the application this class needs to be configured so that the initial
 * configuration of the application may be loaded from the configuration file. The configuration
 * file holds properties stored in XML format, database configuration and user authentication
 * configuration. Use {@link #setHomeDirectory(String)} and {@link #setConfigName(String)} for
 * setting the home directory and path to the configuration file.<p>
 *
 * XML property names must be in the form <code>prop.name</code> - parts of the name must
 * be seperated by ".". The value can be any valid String, including strings with line breaks.
 */
public class SystemGlobals {

	private static final Logger Log = LoggerFactory.getLogger(SystemGlobals.class);
	
    private static String MBASE_CONFIG_FILENAME = "WebContent" + File.separator + "WEB-INF" + File.separator + "classes" + File.separator + "mbase.xml";

    /**
     * Location of the jiveHome directory. All configuration files should be
     * located here.
     */
    private static String home = null;

    public static boolean failedLoading = false;

    private static XMLProperties xmlProperties = null;
    private static Locale locale = null;

    
    /**
     * Returns the global Locale used by Jive. A locale specifies language
     * and country codes, and is used for internationalization. The default
     * locale is system dependant - Locale.getDefault().
     *
     * @return the global locale used by Jive.
     */
    public static Locale getLocale() {
        if (locale == null) {
            if (xmlProperties != null) {
                String [] localeArray;
                String localeProperty = xmlProperties.getProperty("locale");
                if (localeProperty != null) {
                    localeArray = localeProperty.split("_");
                }
                else {
                    localeArray = new String[] {"", ""};
                }

                String language = localeArray[0];
                if (language == null) {
                    language = "";
                }
                String country = "";
                if (localeArray.length == 2) {
                    country = localeArray[1];
                }
                // If no locale info is specified, return the system default Locale.
                if (language.equals("") && country.equals("")) {
                    locale = Locale.getDefault();
                }
                else {
                    locale = new Locale(language, country);
                }
            }
            else {
                return Locale.getDefault();
            }
        }
        return locale;
    }

    /**
     * Sets the global locale used by Jive. A locale specifies language
     * and country codes, and is used for formatting dates and numbers.
     * The default locale is Locale.US.
     *
     * @param newLocale the global Locale for Jive.
     */
    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        // Save values to mbase properties.
        setXMLProperty("locale", locale.toString());
    }


    /**
     * Returns the location of the <code>home</code> directory.
     *
     * @return the location of the home dir.
     */
    public static String getHomeDirectory() {
        if (xmlProperties == null) {
            loadSetupProperties();
        }
        return home;
    }

    /**
     * Sets the location of the <code>home</code> directory. The directory must exist and the
     * user running the application must have read and write permissions over the specified
     * directory.
     *
     * @param pathname the location of the home dir.
     */
    public static void setHomeDirectory(String pathname) {
        File mh = new File(pathname);
        // Do a permission check on the new home directory
        if (!mh.exists()) {
            Log.error("Error - the specified home directory does not exist (" + pathname + ")");
        }
        else if (!mh.canRead() || !mh.canWrite()) {
                Log.error("Error - the user running this application can not read " +
                        "and write to the specified home directory (" + pathname + "). " +
                        "Please grant the executing user read and write permissions.");
        }
        else {
            home = pathname;
        }
    }

    /**
     * Returns a local property. Local properties are stored in the file defined in
     * <tt>MBASE_CONFIG_FILENAME</tt> that exists in the <tt>home</tt> directory.
     * Properties are always specified as "foo.bar.prop", which would map to
     * the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     * @param name the name of the property to return.
     * @return the property value specified by name.
     */
    public static String getXMLProperty(String name) {
        if (xmlProperties == null) {
            loadSetupProperties();
        }

        // home not loaded?
        if (xmlProperties == null) {
            return null;
        }

        return xmlProperties.getProperty(name);
    }

    /**
     * Returns a local property. Local properties are stored in the file defined in
     * <tt>MBASE_CONFIG_FILENAME</tt> that exists in the <tt>home</tt> directory.
     * Properties are always specified as "foo.bar.prop", which would map to
     * the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     * If the specified property can't be found, the <tt>defaultValue</tt> will be returned.
     *
     * @param name the name of the property to return.
     * @param defaultValue the default value for the property.
     * @return the property value specified by name.
     */
    public static String getXMLProperty(String name, String defaultValue) {
        if (xmlProperties == null) {
            loadSetupProperties();
        }

        // home not loaded?
        if (xmlProperties == null) {
            return null;
        }

        String value = xmlProperties.getProperty(name);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Returns an integer value local property. Local properties are stored in the file defined in
     * <tt>MBASE_CONFIG_FILENAME</tt> that exists in the <tt>home</tt> directory.
     * Properties are always specified as "foo.bar.prop", which would map to
     * the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     * If the specified property can't be found, or if the value is not a number, the
     * <tt>defaultValue</tt> will be returned.
     *
     * @param name the name of the property to return.
     * @param defaultValue value returned if the property could not be loaded or was not
     *      a number.
     * @return the property value specified by name or <tt>defaultValue</tt>.
     */
    public static int getXMLProperty(String name, int defaultValue) {
        String value = getXMLProperty(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException nfe) {
                // Ignore.
            }
        }
        return defaultValue;
    }

    /**
     * Returns a boolean value local property. Local properties are stored in the
     * file defined in <tt>MBASE_CONFIG_FILENAME</tt> that exists in the <tt>home</tt>
     * directory. Properties are always specified as "foo.bar.prop", which would map to
     * the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     * If the specified property can't be found, the <tt>defaultValue</tt> will be returned.
     * If the property is found, it will be parsed using {@link Boolean#valueOf(String)}.  
     *
     * @param name the name of the property to return.
     * @param defaultValue value returned if the property could not be loaded or was not
     *      a number.
     * @return the property value specified by name or <tt>defaultValue</tt>.
     */
    public static boolean getXMLProperty(String name, boolean defaultValue) {
        String value = getXMLProperty(name);
        if (value != null) {
            return Boolean.valueOf(value);
        }
        return defaultValue;
    }

    /**
     * Sets a local property. If the property doesn't already exists, a new
     * one will be created. Local properties are stored in the file defined in
     * <tt>MBASE_CONFIG_FILENAME</tt> that exists in the <tt>home</tt> directory.
     * Properties are always specified as "foo.bar.prop", which would map to
     * the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     * @param name the name of the property being set.
     * @param value the value of the property being set.
     */
    public static void setXMLProperty(String name, String value) {
//        if (xmlProperties == null) {
//            loadSetupProperties();
//        }
//
//        // jiveHome not loaded?
//        if (xmlProperties != null) {
//            xmlProperties.setProperty(name, value);
//        }
    }

    /**
     * Sets multiple local properties at once. If a property doesn't already exists, a new
     * one will be created. Local properties are stored in the file defined in
     * <tt>MBASE_CONFIG_FILENAME</tt> that exists in the <tt>home</tt> directory.
     * Properties are always specified as "foo.bar.prop", which would map to
     * the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     * @param propertyMap a map of properties, keyed on property name.
     */
    public static void setXMLProperties(Map<String, String> propertyMap) {
//        if (xmlProperties == null) {
//            loadSetupProperties();
//        }
//
//        if (xmlProperties != null) {
//            xmlProperties.setProperties(propertyMap);
//        }
    }

    /**
     * Return all immediate children property values of a parent local property as a list of strings,
     * or an empty list if there are no children. For example, given
     * the properties <tt>X.Y.A</tt>, <tt>X.Y.B</tt>, <tt>X.Y.C</tt> and <tt>X.Y.C.D</tt>, then
     * the immediate child properties of <tt>X.Y</tt> are <tt>A</tt>, <tt>B</tt>, and
     * <tt>C</tt> (the value of <tt>C.D</tt> would not be returned using this method).<p>
     *
     * Local properties are stored in the file defined in <tt>MBASE_CONFIG_FILENAME</tt> that exists
     * in the <tt>home</tt> directory. Properties are always specified as "foo.bar.prop",
     * which would map to the following entry in the XML file:
     * <pre>
     * &lt;foo&gt;
     *     &lt;bar&gt;
     *         &lt;prop&gt;some value&lt;/prop&gt;
     *     &lt;/bar&gt;
     * &lt;/foo&gt;
     * </pre>
     *
     *
     * @param parent the name of the parent property to return the children for.
     * @return all child property values for the given parent.
     */
    public static List getXMLProperties(String parent) {
        if (xmlProperties == null) {
            loadSetupProperties();
        }

        // jiveHome not loaded?
        if (xmlProperties == null) {
            return Collections.EMPTY_LIST;
        }

        String[] propNames = xmlProperties.getChildrenProperties(parent);
        List<String> values = new ArrayList<String>();
        for (String propName : propNames) {
            String value = getXMLProperty(parent + "." + propName);
            if (value != null) {
                values.add(value);
            }
        }

        return values;
    }

    /**
     * Deletes a locale property. If the property doesn't exist, the method
     * does nothing.
     *
     * @param name the name of the property to delete.
     */
    public static void deleteXMLProperty(String name) {
        if (xmlProperties == null) {
            loadSetupProperties();
        }
        xmlProperties.deleteProperty(name);
    }


   /**
    * Allows the name of the local config file name to be changed. The
    * default is "mbase.xml".
    *
    * @param configName the name of the config file.
    */
    public static void setConfigName(String configName) {
        MBASE_CONFIG_FILENAME = configName;
    }

    /**
     * Returns the name of the local config file name.
     *
     * @return the name of the config file.
     */
    static String getConfigName() {
        return MBASE_CONFIG_FILENAME;
    }

    /**
     * Loads properties if necessary. Property loading must be done lazily so
     * that we give outside classes a chance to set <tt>home</tt>.
     */
    private synchronized static void loadSetupProperties() {
        if (xmlProperties == null) {
            // If home is null then log that the application will not work correctly
            if (home == null && !failedLoading) {
                failedLoading = true;
                StringBuilder msg = new StringBuilder();
                msg.append("Critical Error! The home directory has not been configured, \n");
                msg.append("which will prevent the application from working correctly.\n\n");
                System.err.println(msg.toString());
            }
            // Create a manager with the full path to the xml config file.
            else {
                try {
                	System.out.println(home + File.separator + getConfigName());
                    xmlProperties = new XMLProperties(home + File.separator + getConfigName());
                }
                catch (IOException ioe) {
                    Log.error(ioe.getMessage(), ioe);
                    failedLoading = true;
                }
            }
        }
    }
}