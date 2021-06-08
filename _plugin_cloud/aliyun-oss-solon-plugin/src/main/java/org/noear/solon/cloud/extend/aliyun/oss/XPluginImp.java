package org.noear.solon.cloud.extend.aliyun.oss;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.aliyun.oss.service.CloudFileServiceOssImp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (OssProps.instance.getFileEnable() == false) {
            return;
        }

        if (Utils.isEmpty(OssProps.instance.getFileAccessKey())) {
            return;
        }

        CloudManager.register(CloudFileServiceOssImp.getInstance());
    }
}
