package com.heima.wemedia.config;

/**
 * ClassName: DeclarePubArticleRabbitConfig
 * Package: com.heima.wemedia.config
 * Description:
 *
 * @Author R
 * @Create 2024/1/4 14:49
 * @Version 1.0
 */

import message.PublishArticleConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 声明定时发布文章
 * 所需的 所有交换机  队列 及 绑定关系
 **/
@Configuration
public class DeclarePubArticleRabbitConfig {
    @Bean
    public DirectExchange delayExchange() {
        return ExchangeBuilder.directExchange(PublishArticleConstants.DELAY_DIRECT_EXCHANGE)
                .delayed()
                .durable(true)
                .build();
    }

    //声明发布文章队列
    @Bean
    public Queue publicArticleQueue() {
        return new Queue(PublishArticleConstants.PUBLISH_ARTICLE_QUEUE,true);
    }

    //绑定
    @Bean
    public Binding bindingDeadQueue() {
        return BindingBuilder.bind(publicArticleQueue()).to(delayExchange()).with(PublishArticleConstants.PUBLISH_ARTICLE_ROUTE_KEY);
    }

}
