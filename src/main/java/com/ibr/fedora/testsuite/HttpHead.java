/**
 * @author Jorge Abrego, Fernando Cardoza
 */
/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibr.fedora.testsuite;

import com.ibr.fedora.TestSuiteGlobals;
import com.ibr.fedora.TestsLabels;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class HttpHead {
public TestsLabels tl = new TestsLabels();

/**
 * @param host
 */
@Test(priority = 15)
@Parameters({"param1"})
public void httpHeadResponseNoBody(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n15." + tl.httpHeadResponseNoBody()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();
    RestAssured.given()
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .head(resource)
            .then()
            .log().all()
            .statusCode(200).assertThat().body(equalTo(""));

    ps.append("\n -Case End- \n").close();
}

/**
 * @param host
 */
@Test(priority = 16)
@Parameters({"param1"})
public void httpHeadResponseHeadersSameAsHttpGet(final String host) throws FileNotFoundException {
    final PrintStream ps = TestSuiteGlobals.logFile();
    ps.append("\n16." + tl.httpHeadResponseHeadersSameAsHttpGet()[1]).append("\n");
    ps.append("Request:\n");
    final String resource =
            RestAssured.given()
                    .contentType("text/turtle")
                    .when()
                    .post(host).asString();

    final Headers headers =
            RestAssured.given()
                    .when()
                    .get(resource).getHeaders();
    final List<Header> hl = new ArrayList<>();
    for (Header h : headers) {
        if (!TestSuiteGlobals.checkPayloadHeader(h.getName())) {
            hl.add(h);
        }
    }

    final ResponseSpecBuilder spec = new ResponseSpecBuilder();
    for (Header h : hl) {
        spec.expectHeader(h.getName(), h.getValue());
    }
    final ResponseSpecification rs = spec.build();

    RestAssured.given()
            .config(RestAssured.config().logConfig(new LogConfig().defaultStream(ps)))
            .log().all()
            .when()
            .head(resource)
            .then()
            .spec(rs)
            .log().all();

    ps.append("\n -Case End- \n").close();
}

}