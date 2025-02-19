package org.apache.ibatis.solon.integration;

import org.apache.ibatis.session.SqlSession;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.apache.ibatis.solon.MybatisAdapter;

import javax.sql.DataSource;

public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) {

        context.subWrapsOfType(DataSource.class, bw->{
            MybatisAdapterManager.register(bw);
        });

        //for new
        context.beanBuilderAdd(org.apache.ibatis.solon.annotation.Db.class, (clz, wrap, anno) -> {
            builderAddDo(clz, wrap,anno.value());
        });

        context.beanInjectorAdd(org.apache.ibatis.solon.annotation.Db.class, (varH, anno) -> {
            injectorAddDo(varH, anno.value());
        });
    }

    private void builderAddDo(Class<?> clz, BeanWrap wrap, String annoValue) {
        if (clz.isInterface() == false) {
            return;
        }

        if (Utils.isEmpty(annoValue)) {
            wrap.context().getWrapAsync(DataSource.class, (dsBw) -> {
                create0(clz, dsBw);
            });
        } else {
            wrap.context().getWrapAsync(annoValue, (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    create0(clz, dsBw);
                }
            });
        }
    }

    private void injectorAddDo(VarHolder varH, String annoValue) {
        if (Utils.isEmpty(annoValue)) {
            varH.context().getWrapAsync(DataSource.class, (dsBw) -> {
                inject0(varH, dsBw);
            });
        } else {
            varH.context().getWrapAsync(annoValue, (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    inject0(varH, dsBw);
                }
            });
        }
    }


    private void create0(Class<?> clz, BeanWrap dsBw) {
        SqlSession session = MybatisAdapterManager.get(dsBw).getFactory().openSession();

        Object raw = session.getMapper(clz);
        dsBw.context().wrapAndPut(clz, raw);
    }

    private void inject0(VarHolder varH, BeanWrap dsBw) {
        MybatisAdapter adapter = MybatisAdapterManager.get(dsBw);

        if (adapter != null) {
            adapter.injectTo(varH);
        }
    }
}
