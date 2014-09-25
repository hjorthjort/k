// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.backend.java.symbolic;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.kframework.backend.java.kil.Definition;
import org.kframework.backend.java.ksimulation.Simulator;
import org.kframework.kil.KSequence;
import org.kframework.kil.Production;
import org.kframework.krun.KRunFrontEnd;
import org.kframework.krun.tools.Debugger;
import org.kframework.krun.tools.Executor;
import org.kframework.krun.tools.GuiDebugger;
import org.kframework.krun.tools.Prover;
import org.kframework.main.FrontEnd;
import org.kframework.utils.BaseTestCase;
import org.kframework.utils.inject.Main;
import org.mockito.Mock;

import com.google.common.collect.HashMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import java.util.List;

public class JavaSymbolicKRunModuleTest extends BaseTestCase {

    @Mock
    Definition definition;

    @Before
    public void setUp() {
        context.klabels = HashMultimap.<String, Production>create();
        context.configVarSorts = new HashMap<>();
        when(rp.runParserOrDie("kast", "foo.c", false, null, context)).thenReturn(KSequence.EMPTY);
    }

    @Test
    public void testCreateInjectionJava() {
        context.kompileOptions.backend = "java";
        String[] argv = new String[] { "foo.c" };
        Module[] definitionSpecificModules = KRunFrontEnd.getDefinitionSpecificModules(argv);
        Module definitionSpecificModuleOverride = Modules.override(definitionSpecificModules).with(new TestModule());
        List<Module> modules = KRunFrontEnd.getModules(argv, definitionSpecificModuleOverride);
        Injector injector = Guice.createInjector(modules);
        assertTrue(injector.getInstance(FrontEnd.class) instanceof KRunFrontEnd);
        injector.getInstance(Key.get(Executor.class, Main.class));
        injector.getInstance(Key.get(Debugger.class, Main.class));
        injector.getInstance(Key.get(GuiDebugger.class, Main.class));
        injector.getInstance(Key.get(Prover.class, Main.class));
        injector.getInstance(Key.get(Simulator.class, Main.class));
    }

    public class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new BaseTestCase.TestModule());
            bind(Definition.class).toInstance(definition);
        }
    }
}