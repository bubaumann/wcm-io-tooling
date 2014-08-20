/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
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
 * #L%
 */
package io.wcm.sling.models.injectors.impl;

import io.wcm.sling.models.annotations.SlingObject;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;

/**
 * Injects common Sling objects that can be derived from either a SlingHttpServletRequest, a ResourceResolver or a
 * Resource.
 * The injection is class-based.
 * <p>
 * Supports the following objects:
 * <table>
 * <tr>
 * <th style="text-align:left">Class</th>
 * <th style="text-align:left">Description</th>
 * <th style="text-align:center">Request</th>
 * <th style="text-align:center">ResourceResolver</th>
 * <th style="text-align:center">Resource</th>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link ResourceResolver}</td>
 * <td>Resource resolver</td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X</td>
 * <td style="text-align:center">X</td>
 * </tr>
 * <tr>
 * <td>{@link Resource}</td>
 * <td>Resource</td>
 * <td style="text-align:center">X</td>
 * <td></td>
 * <td style="text-align:center">X</td>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link SlingHttpServletRequest}</td>
 * <td>Sling request</td>
 * <td style="text-align:center">X</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>{@link SlingHttpServletResponse}</td>
 * <td>Sling response</td>
 * <td style="text-align:center">X</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr style="background-color:#eee">
 * <td>{@link SlingScriptHelper}</td>
 * <td>Sling script helper</td>
 * <td style="text-align:center">X</td>
 * <td></td>
 * <td></td>
 * </tr>
 * </table>
 */
@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 500)
public final class SlingObjectInjector implements Injector, InjectAnnotationProcessorFactory, AcceptsNullName {

  /**
   * Injector name
   */
  public static final String NAME = "wcm-io-sling-object";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object getValue(final Object adaptable, final String name, final Type type, final AnnotatedElement element,
      final DisposalCallbackRegistry callbackRegistry) {

    // only class types are supported
    if (!(type instanceof Class<?>)) {
      return null;
    }
    Class<?> requestedClass = (Class<?>)type;

    // validate input
    if (adaptable instanceof SlingHttpServletRequest) {
      SlingHttpServletRequest request = (SlingHttpServletRequest)adaptable;
      if (requestedClass.equals(ResourceResolver.class)) {
        return request.getResourceResolver();
      }
      if (requestedClass.equals(Resource.class)) {
        return request.getResource();
      }
      if (requestedClass.equals(SlingHttpServletRequest.class)) {
        return request;
      }
      if (requestedClass.equals(SlingHttpServletResponse.class)) {
        return getSlingHttpServletResponse(request);
      }
      if (requestedClass.equals(SlingScriptHelper.class)) {
        return getSlingScriptHelper(request);
      }
    }
    else if (adaptable instanceof ResourceResolver) {
      ResourceResolver resourceResolver = (ResourceResolver)adaptable;
      if (requestedClass.equals(ResourceResolver.class)) {
        return resourceResolver;
      }
    }
    else if (adaptable instanceof Resource) {
      Resource resource = (Resource)adaptable;
      if (requestedClass.equals(ResourceResolver.class)) {
        return resource.getResourceResolver();
      }
      if (requestedClass.equals(Resource.class)) {
        return resource;
      }
    }

    return null;
  }

  private SlingScriptHelper getSlingScriptHelper(final SlingHttpServletRequest request) {
    SlingBindings bindings = (SlingBindings)request.getAttribute(SlingBindings.class.getName());
    if (bindings != null) {
      return bindings.getSling();
    }
    return null;
  }

  private SlingHttpServletResponse getSlingHttpServletResponse(final SlingHttpServletRequest request) {
    SlingScriptHelper scriptHelper = getSlingScriptHelper(request);
    if (scriptHelper != null) {
      return scriptHelper.getResponse();
    }
    return null;
  }

  @Override
  public InjectAnnotationProcessor createAnnotationProcessor(final Object adaptable, final AnnotatedElement element) {
    // check if the element has the expected annotation
    SlingObject annotation = element.getAnnotation(SlingObject.class);
    if (annotation != null) {
      return new SlingObjectAnnotationProcessor(annotation);
    }
    return null;
  }

  private static class SlingObjectAnnotationProcessor extends AbstractInjectAnnotationProcessor {

    private final SlingObject annotation;

    public SlingObjectAnnotationProcessor(final SlingObject annotation) {
      this.annotation = annotation;
    }

    @Override
    public Boolean isOptional() {
      return this.annotation.optional();
    }
  }

}
