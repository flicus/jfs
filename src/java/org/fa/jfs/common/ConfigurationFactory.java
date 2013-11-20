/*
 * The MIT License
 *
 * Copyright (c) 2013  Sergey Skoptsov (flicus@gmail.com), Alexey Marin (asmadews@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.fa.jfs.common;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationFactory {

    public static final Logger log = Logger.getLogger(ConfigurationFactory.class);
    public static final String CONFIGURATION_READER = "org.fa.jfs.configuration.reader";
    public static final String CONFIGURATION_PATH = "org.fa.jfs.configuration.path";

    private Map<String, Configuration> configurations = new HashMap<String, Configuration>();

    private ConfigurationReader reader;


    public static ConfigurationFactory getInstance() {
        return Singleton.instance;
    }

    private static final class Singleton {
        final static ConfigurationFactory instance = new ConfigurationFactory();
    }

    public ConfigurationFactory() {
    }

    public ConfigurationFactory setReader(ConfigurationReader reader) {
        this.reader = reader;
        return this;
    }

    public Configuration getConfiguration(String filePath) {
        Configuration configuration = configurations.get(filePath);
        if (configuration == null) {
            if (reader == null) reader = getDefaultReader();
            configuration = reader.read(filePath);
            configurations.put(filePath, configuration);
        }
        return configuration;
    }

    public Configuration getConfiguration() {
        String configurationPath = System.getProperty(CONFIGURATION_PATH);
        if (configurationPath == null) configurationPath = "./configuration.xml";
        return getConfiguration(configurationPath);
    }

    private ConfigurationReader getDefaultReader() {
        ConfigurationReader reader = null;
        String readerClassName = System.getProperty(CONFIGURATION_READER);
        if (readerClassName == null)
            readerClassName = "org.fa.jfs.common.XStreamConfigurationReader";
        try {
            Class<? extends ConfigurationReader> readerClass = (Class<ConfigurationReader>) Class.forName(readerClassName);
            reader = readerClass.newInstance();
        } catch (ClassNotFoundException e) {
            log.error(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            log.error(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            log.error(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return reader;
    }
}
