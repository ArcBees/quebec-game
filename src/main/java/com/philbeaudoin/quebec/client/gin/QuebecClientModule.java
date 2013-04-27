/**
 * Copyright 2012 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.quebec.client.gin;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.Provides;
import com.gwtplatform.dispatch.shared.SecurityCookie;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.philbeaudoin.quebec.client.PlaceManager;
import com.philbeaudoin.quebec.client.admin.AdminPresenter;
import com.philbeaudoin.quebec.client.admin.AdminSignInPresenter;
import com.philbeaudoin.quebec.client.admin.AdminSignInView;
import com.philbeaudoin.quebec.client.admin.AdminView;
import com.philbeaudoin.quebec.client.game.GameControllerFactories;
import com.philbeaudoin.quebec.client.interaction.InteractionFactories;
import com.philbeaudoin.quebec.client.main.GamePresenter;
import com.philbeaudoin.quebec.client.main.GameView;
import com.philbeaudoin.quebec.client.menu.MenuPresenter;
import com.philbeaudoin.quebec.client.menu.MenuView;
import com.philbeaudoin.quebec.client.playerAgent.PlayerAgentFactories;
import com.philbeaudoin.quebec.client.renderer.RendererFactories;
import com.philbeaudoin.quebec.client.scene.SceneNodeAnimation;
import com.philbeaudoin.quebec.client.session.ClientSessionManager;
import com.philbeaudoin.quebec.client.session.ClientSessionManagerImpl;
import com.philbeaudoin.quebec.client.utils.GwtRandomShuffler;
import com.philbeaudoin.quebec.shared.Constants;
import com.philbeaudoin.quebec.shared.game.state.Shuffler;

/**
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class QuebecClientModule extends AbstractPresenterModule {

  @Override
  protected void configure() {
    // Install factories for assisted injection.
    install(new GinFactoryModuleBuilder().build(RendererFactories.class));
    install(new GinFactoryModuleBuilder().build(InteractionFactories.class));
    install(new GinFactoryModuleBuilder().build(PlayerAgentFactories.class));
    install(new GinFactoryModuleBuilder().build(SceneNodeAnimation.Factory.class));
    install(new GinFactoryModuleBuilder().build(GameControllerFactories.class));

    bind(Shuffler.class).to(GwtRandomShuffler.class);

    bindConstant().annotatedWith(SecurityCookie.class).to(Constants.securityCookieName);

    install(new DefaultModule(PlaceManager.class));

    bindPresenter(GamePresenter.class, GamePresenter.MyView.class, GameView.class,
        GamePresenter.MyProxy.class);
    bindPresenter(MenuPresenter.class, MenuPresenter.MyView.class, MenuView.class,
        MenuPresenter.MyProxy.class);
    bindPresenter(AdminSignInPresenter.class, AdminSignInPresenter.MyView.class,
        AdminSignInView.class, AdminSignInPresenter.MyProxy.class);
    bindPresenter(AdminPresenter.class, AdminPresenter.MyView.class, AdminView.class,
        AdminPresenter.MyProxy.class);

    bind(ClientSessionManagerImpl.class).asEagerSingleton();
    bind(ClientSessionManager.class).to(ClientSessionManagerImpl.class);
  }

  @Provides
  public Scheduler getScheduler() {
    return Scheduler.get();
  }

  @Provides
  public AnimationScheduler getAnimationScheduler() {
    return AnimationScheduler.get();
  }
}
