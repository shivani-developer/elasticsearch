/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.gradle.test.rest.transform.warnings;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elasticsearch.gradle.test.rest.transform.RestTestContext;
import org.elasticsearch.gradle.test.rest.transform.RestTestTransformByParentObject;
import org.elasticsearch.gradle.test.rest.transform.feature.FeatureInjector;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

import java.util.List;
import java.util.Objects;

/**
 * A transformation to inject an expected warning for a given test.
 */
public class InjectWarnings extends FeatureInjector implements RestTestTransformByParentObject {

    private static JsonNodeFactory jsonNodeFactory = JsonNodeFactory.withExactBigDecimals(false);

    private final List<String> warnings;
    private final String testName;

    /**
     * @param warnings The warnings to inject
     * @param testName The testName to inject
     */
    public InjectWarnings(List<String> warnings, String testName) {
        this.warnings = warnings;
        this.testName = Objects.requireNonNull(testName, "inject warnings is only supported for named tests");
    }

    @Override
    public void transformTest(ObjectNode doNodeParent) {
        ObjectNode doNodeValue = (ObjectNode) doNodeParent.get(getKeyToFind());
        ArrayNode arrayWarnings = (ArrayNode) doNodeValue.get("warnings");
        if (arrayWarnings == null) {
            arrayWarnings = new ArrayNode(jsonNodeFactory);
            doNodeValue.set("warnings", arrayWarnings);
        }
        warnings.forEach(arrayWarnings::add);
    }

    @Override
    @Internal
    public String getKeyToFind() {
        return "do";
    }

    @Override
    @Internal
    public String getSkipFeatureName() {
        return "warnings";
    }

    @Override
    public boolean shouldApply(RestTestContext testContext) {
        return testName.equals(testContext.getTestName());
    }

    @Input
    public List<String> getWarnings() {
        return warnings;
    }

    @Input
    public String getTestName() {
        return testName;
    }
}
