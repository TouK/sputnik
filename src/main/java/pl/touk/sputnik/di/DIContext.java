package pl.touk.sputnik.di;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import pl.touk.sputnik.review.locator.BuildDirLocatorFactory;

public class DIContext {

    public static InjectionContext context() {
        InjectionContext context = new DefaultInjectionContext();
        context.registerBean(BuildDirLocatorFactory.create());
        context.freeze();
        return context;
    }

}
