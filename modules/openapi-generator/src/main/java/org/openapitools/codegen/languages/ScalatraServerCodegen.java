/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 * Copyright 2018 SmartBear Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen.languages;

import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.features.*;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;

import java.io.File;
import java.util.*;

public class ScalatraServerCodegen extends AbstractScalaCodegen implements CodegenConfig {

    protected String groupId = "org.openapitools";
    protected String artifactId = "openapi-server";
    protected String artifactVersion = "1.0.0";

    public ScalatraServerCodegen() {
        super();

        modifyFeatureSet(features -> features
                .includeDocumentationFeatures(DocumentationFeature.Readme)
                .wireFormatFeatures(EnumSet.of(WireFormatFeature.JSON, WireFormatFeature.XML, WireFormatFeature.Custom))
                .securityFeatures(EnumSet.noneOf(SecurityFeature.class))
                .excludeGlobalFeatures(
                        GlobalFeature.XMLStructureDefinitions,
                        GlobalFeature.Callbacks,
                        GlobalFeature.LinkObjects,
                        GlobalFeature.ParameterStyling
                )
                .excludeSchemaSupportFeatures(
                        SchemaSupportFeature.Polymorphism
                )
                .excludeParameterFeatures(
                        ParameterFeature.Cookie
                )
        );

        outputFolder = "generated-code/scalatra";
        modelTemplateFiles.put("model.mustache", ".scala");
        apiTemplateFiles.put("api.mustache", ".scala");
        embeddedTemplateDir = templateDir = "scalatra";
        invokerPackage = "org.openapitools";
        apiPackage = "org.openapitools.server.api";
        modelPackage = "org.openapitools.server.model";

        defaultIncludes = new HashSet<>(
                Arrays.asList("double",
                        "Int",
                        "Long",
                        "Float",
                        "Double",
                        "char",
                        "float",
                        "String",
                        "boolean",
                        "Boolean",
                        "Double",
                        "Integer",
                        "Long",
                        "Float",
                        "Set",
                        "Map")
        );

        typeMapping = new HashMap<>();
        typeMapping.put("array", "List");
        typeMapping.put("set", "Set");
        typeMapping.put("boolean", "Boolean");
        typeMapping.put("string", "String");
        typeMapping.put("int", "Int");
        typeMapping.put("integer", "Int");
        typeMapping.put("long", "Long");
        typeMapping.put("float", "Float");
        typeMapping.put("byte", "Byte");
        typeMapping.put("short", "Short");
        typeMapping.put("char", "Char");
        typeMapping.put("double", "Double");
        typeMapping.put("object", "Any");
        typeMapping.put("file", "File");
        typeMapping.put("binary", "File");
        typeMapping.put("number", "Double");
        typeMapping.put("decimal", "BigDecimal");

        importMapping = new HashMap<String, String>();
        importMapping.put("UUID", "java.util.UUID");
        importMapping.put("URI", "java.net.URI");
        importMapping.put("File", "java.io.File");
        importMapping.put("Date", "java.util.Date");
        importMapping.put("Timestamp", "java.sql.Timestamp");
        importMapping.put("Map", "java.util.Map");
        importMapping.put("HashMap", "java.util.HashMap");
        importMapping.put("Array", "java.util.List");
        importMapping.put("ArrayList", "java.util.ArrayList");
        importMapping.put("DateTime", "org.joda.time.DateTime");
        importMapping.put("LocalDateTime", "org.joda.time.LocalDateTime");
        importMapping.put("LocalDate", "org.joda.time.LocalDate");
        importMapping.put("LocalTime", "org.joda.time.LocalTime");
        importMapping.put("ListBuffer", "scala.collection.mutable.ListBuffer");
        importMapping.put("Set", "scala.collection.immutable.Set");
        importMapping.put("ListSet", "scala.collection.immutable.ListSet");

        instantiationTypes.put("array", "List");
        instantiationTypes.put("map", "HashMap");
        instantiationTypes.put("set", "Set");
    }

    @Override
    public void processOpts() {
        super.processOpts();

        String appPackage = invokerPackage + ".app";

        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
        supportingFiles.add(new SupportingFile("build.mustache", "", "build.sbt"));
        supportingFiles.add(new SupportingFile("web.xml", "/src/main/webapp/WEB-INF", "web.xml"));
        supportingFiles.add(new SupportingFile("logback.xml", "/src/main/resources", "logback.xml"));
        supportingFiles.add(new SupportingFile("gitignore.mustache", "", ".gitignore"));
        supportingFiles.add(new SupportingFile("JettyMain.mustache", sourceFolderByPackage(invokerPackage), "JettyMain.scala"));
        supportingFiles.add(new SupportingFile("Bootstrap.mustache", sourceFolderByPackage(invokerPackage), "ScalatraBootstrap.scala"));
        supportingFiles.add(new SupportingFile("ServletApp.mustache", sourceFolderByPackage(appPackage), "ServletApp.scala"));
        supportingFiles.add(new SupportingFile("project/build.properties", "project", "build.properties"));
        supportingFiles.add(new SupportingFile("project/plugins.sbt", "project", "plugins.sbt"));
        supportingFiles.add(new SupportingFile("sbt", "", "sbt"));

        additionalProperties.put("appName", appName);
        additionalProperties.put("appDescription", appDescription);
        additionalProperties.put("infoUrl", infoUrl);
        additionalProperties.put("infoEmail", infoEmail);
        additionalProperties.put("apiVersion", apiVersion);
        additionalProperties.put("licenseInfo", licenseInfo);
        additionalProperties.put("licenseUrl", licenseUrl);
        additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        additionalProperties.put(CodegenConstants.GROUP_ID, groupId);
        additionalProperties.put(CodegenConstants.ARTIFACT_ID, artifactId);
        additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, artifactVersion);
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.SERVER;
    }

    @Override
    public String getName() {
        return "scalatra";
    }

    @Override
    public String getHelp() {
        return "Generates a Scala server application with Scalatra.";
    }

    @Override
    public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
        OperationMap operations = objs.getOperations();
        List<CodegenOperation> operationList = operations.getOperation();
        for (CodegenOperation op : operationList) {
            // force http method to lower case
            op.httpMethod = op.httpMethod.toLowerCase(Locale.ROOT);

            String[] items = op.path.split("/", -1);
            String scalaPath = "";
            int pathParamIndex = 0;

            for (int i = 0; i < items.length; ++i) {
                if (items[i].matches("^\\{(.*)\\}$")) { // wrap in {}
                    scalaPath = scalaPath + ":" + items[i].replace("{", "").replace("}", "");
                    pathParamIndex++;
                } else {
                    scalaPath = scalaPath + items[i];
                }

                if (i != items.length - 1) {
                    scalaPath = scalaPath + "/";
                }
            }

            op.vendorExtensions.put("x-scalatra-path", scalaPath);
        }

        return objs;
    }

    private String sourceFolderByPackage(String packageName) {
        return sourceFolder + File.separator + packageName.replace('.', File.separatorChar);
    }
}
