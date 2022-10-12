import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.tools.ant.*;

/*
 * Copyright (C) 2007, Trygve Laugstøl
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

public class ModelloTask extends Task {
    private String model;
    private String plugin;
    private String output;
    private String version;
    private boolean packageWithVersion;
    private boolean useJava5;
    private String encoding = "UTF-8";
    private List classpath = new ArrayList();

    public void execute() throws BuildException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try
        {
            work();
        }
        catch( Exception e )
        {
            // Temporary logging - until it's all working.
            System.err.println("Exception: " + e);
            e.printStackTrace();
            throw new BuildException( "Error while invoking Modello", e );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( cl );
        }
    }

    private void addToClassPath(String jar) throws Exception {
        if (! new File(jar).exists()) {
            System.err.println("Cannot find file " + jar);
			System.exit(1);
        }
        classpath.add(new URL( "file:" + jar));
    }

    private URL[] getClassPath() {
        return (URL[]) classpath.toArray(new URL[classpath.size()]);
    }

    private void work() throws Exception {
        log( "Running the '" + plugin + "' Modello plugin using model file " + model + " for version " + version );

        addToClassPath("/usr/share/java/plexus-build-api.jar");
        addToClassPath("/usr/share/java/plexus-utils2.jar");
        addToClassPath("/usr/share/java/plexus-classworlds.jar");
        addToClassPath("/usr/share/java/plexus-container-default.jar");
        addToClassPath("/usr/share/java/modello-core.jar");
        addToClassPath("/usr/share/java/modello-plugin-converters.jar");
        addToClassPath("/usr/share/java/modello-plugin-dom4j.jar");
        addToClassPath("/usr/share/java/modello-plugin-java.jar");
        addToClassPath("/usr/share/java/modello-plugin-jdom.jar");
            // new URL( "file:/usr/share/java/modello-plugin-jpox.jar");
            // new URL( "file:/usr/share/java/modello-plugin-plexus-registry.jar");
        addToClassPath("/usr/share/java/modello-plugin-stax.jar");
            // new URL( "file:/usr/share/java/modello-plugin-store.jar");
        addToClassPath("/usr/share/java/modello-plugin-xdoc.jar");
        addToClassPath("/usr/share/java/modello-plugin-xml.jar");
        addToClassPath("/usr/share/java/modello-plugin-xpp3.jar");
        addToClassPath("/usr/share/java/modello-plugin-xsd.jar");
        addToClassPath("/usr/share/java/guava.jar");
        addToClassPath("/usr/share/java/xbean-reflect.jar");

        ClassLoader cl = new URLClassLoader( getClassPath() );

        Thread.currentThread().setContextClassLoader( cl );

        String[] args = new String[]{
            new File( getProject().getBaseDir(), model ).getAbsolutePath(),
            plugin,
            output,
            version,
            Boolean.toString( packageWithVersion ),
            Boolean.toString( useJava5 ),
            encoding};

		try {
	        Class modelloCli = cl.loadClass( "org.codehaus.modello.ModelloCli" );
	        Method main = modelloCli.getMethod( "main", new Class[] { String[].class } );
	        main.invoke( null, new Object[] { args } );
		} catch (Error e) {
			e.printStackTrace();
			throw e;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
    }

    public void setModel( String model ) {
        this.model = model;
    }

    public void setPlugin( String plugin ) {
        this.plugin = plugin;
    }

    public void setOutput( String output ) {
        this.output = output;
    }

    public void setVersion( String version ) {
        this.version = version;
    }

    public void setPackageWithVersion( boolean packageWithVersion ) {
        this.packageWithVersion = packageWithVersion;
    }

    public void setUseJava5( boolean useJava5 ) {
        this.useJava5 = useJava5;
    }

    public void setEncoding( String encoding ) {
        this.encoding = encoding;
    }

}
