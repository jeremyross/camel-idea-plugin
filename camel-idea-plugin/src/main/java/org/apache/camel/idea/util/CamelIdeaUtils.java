/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.idea.util;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import org.apache.camel.idea.service.CamelCatalogService;

import static org.apache.camel.idea.util.IdeaUtils.isElementFromSetterProperty;
import static org.apache.camel.idea.util.IdeaUtils.isFromConstructor;

/**
 * Utility methods to work with Camel related {@link com.intellij.psi.PsiElement} elements.
 * <p/>
 * This class is only for Camel related IDEA APIs. If you need only IDEA APIs then use {@link IdeaUtils} instead.
 */
public final class CamelIdeaUtils {

    private CamelIdeaUtils() {
    }

    /**
     * Is the given element from the start of a Camel route, eg <tt>from</tt>, ot &lt;from&gt;.
     */
    public static boolean isCamelRouteStart(PsiElement element) {
        // java method call
        if (IdeaUtils.isFromJavaMethodCall(element, "from", "fromF")) {
            return true;
        }
        // xml
        XmlTag xml = PsiTreeUtil.getParentOfType(element, XmlTag.class);
        if (xml != null) {
            String name = xml.getLocalName();
            XmlTag parentTag = xml.getParentTag();
            if (parentTag != null) {
                return "from".equals(name) && "route".equals(parentTag.getLocalName());
            }
        }
        // groovy
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Groovy")) {
                return IdeaUtils.isFromGroovyMethod(element, "from", "fromF");
            }
        }
        // kotlin
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("kotlin")) {
                return IdeaUtils.isFromKotlinMethod(element, "from", "fromF");
            }
        }
        // scala
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Scala")) {
                return IdeaUtils.isFromScalaMethod(element, "from", "fromF");
            }
        }

        return false;
    }

    /**
     * Is the given element a simple of a Camel route, eg <tt>simple</tt>, ot &lt;simple&gt;.
     */
    public static boolean isCamelRouteSimpleExpression(PsiElement element) {
        // java method call
        if (IdeaUtils.isFromJavaMethodCall(element, "simple")) {
            return true;
        }
        // xml
        XmlTag xml = PsiTreeUtil.getParentOfType(element, XmlTag.class);
        if (xml != null) {
            String name = xml.getLocalName();
            XmlTag parentTag = xml.getParentTag();
            if (parentTag != null) {
                return "simple".equals(name) && "simple".equals(parentTag.getLocalName());
            }
        }
        // groovy
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Groovy")) {
                return IdeaUtils.isFromGroovyMethod(element, "simple");
            }
        }
        // kotlin
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("kotlin")) {
                return IdeaUtils.isFromKotlinMethod(element, "simple");
            }
        }
        // scala
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Scala")) {
                return IdeaUtils.isFromScalaMethod(element, "simple");
            }
        }

        return false;
    }

    /**
     * Is the given element from a consumer endpoint used in a route from a <tt>from</tt>, <tt>fromF</tt>,
     * <tt>interceptFrom</tt>, or <tt>pollEnrich</tt> pattern.
     */
    public static boolean isConsumerEndpoint(PsiElement element) {
        // java method call
        if (IdeaUtils.isFromJavaMethodCall(element, "from", "fromF", "interceptFrom", "pollEnrich")) {
            return true;
        }
        // annotation
        PsiAnnotation annotation = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
        if (annotation != null && annotation.getQualifiedName() != null) {
            return annotation.getQualifiedName().equals("org.apache.camel.Consume");
        }
        // xml
        XmlTag xml = PsiTreeUtil.getParentOfType(element, XmlTag.class);
        if (xml != null) {
            return IdeaUtils.isFromXmlTag(xml, "pollEnrich", "from", "interceptFrom");
        }
        // groovy
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Groovy")) {
                return IdeaUtils.isFromGroovyMethod(element, "from", "fromF", "interceptFrom", "pollEnrich");
            }
        }
        // kotlin
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("kotlin")) {
                return IdeaUtils.isFromKotlinMethod(element, "from", "fromF", "interceptFrom", "pollEnrich");
            }
        }
        // scala
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Scala")) {
                return IdeaUtils.isFromScalaMethod(element, "from", "fromF", "interceptFrom", "pollEnrich");
            }
        }

        return false;
    }

    /**
     * Is the given element from a producer endpoint used in a route from a <tt>to</tt>, <tt>toF</tt>,
     * <tt>interceptSendToEndpoint</tt>, <tt>wireTap</tt>, or <tt>enrich</tt> pattern.
     */
    public static boolean isProducerEndpoint(PsiElement element) {
        // java method call
        if (IdeaUtils.isFromJavaMethodCall(element, "to", "toF", "toD", "enrich", "interceptSendToEndpoint", "wireTap", "deadLetterChannel")) {
            return true;
        }
        // annotation
        PsiAnnotation annotation = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
        if (annotation != null && annotation.getQualifiedName() != null) {
            return annotation.getQualifiedName().equals("org.apache.camel.Produce");
        }
        // xml
        XmlTag xml = PsiTreeUtil.getParentOfType(element, XmlTag.class);
        if (xml != null) {
            return IdeaUtils.isFromXmlTag(xml, "enrich", "to", "interceptSendToEndpoint", "wireTap", "deadLetterChannel");
        }
        // groovy
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Groovy")) {
                return IdeaUtils.isFromGroovyMethod(element, "to", "toF", "toD", "enrich", "interceptSendToEndpoint", "wireTap", "deadLetterChannel");
            }
        }
        // kotlin
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("kotlin")) {
                return IdeaUtils.isFromKotlinMethod(element, "to", "toF", "toD", "enrich", "interceptSendToEndpoint", "wireTap", "deadLetterChannel");
            }
        }
        // scala
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Scala")) {
                return IdeaUtils.isFromScalaMethod(element, "to", "toF", "toD", "enrich", "interceptSendToEndpoint", "wireTap", "deadLetterChannel");
            }
        }

        return false;
    }

    /**
     * Is the given element from a method call named <tt>fromF</tt> or <tt>toF</tt> which supports the
     * {@link String#format(String, Object...)} syntax and therefore we need special handling.
     */
    public static boolean isFromStringFormatEndpoint(PsiElement element) {
        // java method call
        if (IdeaUtils.isFromJavaMethodCall(element, "fromF", "toF")) {
            return true;
        }
        // groovy
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Groovy")) {
                return IdeaUtils.isFromGroovyMethod(element, "fromF", "toF");
            }
        }
        // kotlin
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("kotlin")) {
                return IdeaUtils.isFromKotlinMethod(element, "fromF", "toF");
            }
        }
        // scala
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (type.getLanguage().isKindOf("Scala")) {
                return IdeaUtils.isFromScalaMethod(element, "fromF", "toF");
            }
        }

        return false;
    }

    /**
     * Is the class a Camel expression class
     *
     * @param clazz  the class
     * @return <tt>true</tt> if its a Camel expression class, <tt>false</tt> otherwise.
     */
    public static boolean isCamelExpressionOrLanguage(PsiClass clazz) {
        if (clazz == null) {
            return false;
        }
        String fqn = clazz.getQualifiedName();
        if ("org.apache.camel.Expression".equals(fqn)
            || "org.apache.camel.Predicate".equals(fqn)
            || "org.apache.camel.model.language.ExpressionDefinition".equals(fqn)
            || "org.apache.camel.builder.ExpressionClause".equals(fqn)) {
            return true;
        }
        // try implements first
        for (PsiClassType ct : clazz.getImplementsListTypes()) {
            PsiClass resolved = ct.resolve();
            if (isCamelExpressionOrLanguage(resolved)) {
                return true;
            }
        }
        // then fallback as extends
        for (PsiClassType ct : clazz.getExtendsListTypes()) {
            PsiClass resolved = ct.resolve();
            if (isCamelExpressionOrLanguage(resolved)) {
                return true;
            }
        }
        // okay then go up and try super
        return isCamelExpressionOrLanguage(clazz.getSuperClass());
    }

    /**
     * Validate if the query contain a known camel component
     */
    public static boolean isQueryContainingCamelComponent(Project project, String query) {
        // is this a possible Camel endpoint uri which we know
        if (query != null && !query.isEmpty()) {
            String componentName = StringUtils.asComponentName(query);
            if (componentName != null && ServiceManager.getService(project, CamelCatalogService.class).get().findComponentNames().contains(componentName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Certain elements should be skipped for endpoint validation such as ActiveMQ brokerURL property and others.
     */
    public static boolean skipEndpointValidation(PsiElement element) {
        if (isElementFromSetterProperty(element, "brokerURL")) {
            return true;
        }
        if (isFromConstructor(element, "ActiveMQConnectionFactory")) {
            return true;
        }

        // skip CXF xml configuration
        XmlTag xml = PsiTreeUtil.getParentOfType(element, XmlTag.class);
        if (xml != null) {
            String ns = xml.getNamespace();
            if (ns.contains("cxf.apache.org")) {
                return true;
            }
        }

        return false;
    }

}
