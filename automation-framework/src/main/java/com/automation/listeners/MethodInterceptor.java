package com.automation.listeners;

import com.automation.annotations.FrameworkAnnotation;
import com.automation.enums.CategoryType;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodInterceptor implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        String categoryParam = context.getCurrentXmlTest().getParameter("category");
        if (categoryParam == null || categoryParam.isEmpty()) {
            return methods;
        }

        Set<String> requestedCategories = Arrays.stream(categoryParam.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        List<IMethodInstance> filteredMethods = new ArrayList<>();
        for (IMethodInstance method : methods) {
            FrameworkAnnotation annotation = method.getMethod()
                    .getConstructorOrMethod()
                    .getMethod()
                    .getAnnotation(FrameworkAnnotation.class);

            if (annotation != null) {
                Set<String> methodCategories = Arrays.stream(annotation.category())
                        .map(CategoryType::name)
                        .collect(Collectors.toSet());

                methodCategories.retainAll(requestedCategories);
                if (!methodCategories.isEmpty()) {
                    filteredMethods.add(method);
                }
            }
        }

        return filteredMethods;
    }
}
