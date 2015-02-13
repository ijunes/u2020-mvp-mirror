package ru.ltst.u2020mvp;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.jimulabs.mirrorsandbox.MirrorAnimatorSandbox;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import ru.ltst.u2020mvp.data.DebugDataModule;
import ru.ltst.u2020mvp.data.GalleryDatabase;
import ru.ltst.u2020mvp.data.api.model.request.Section;
import ru.ltst.u2020mvp.data.api.model.response.Image;
import ru.ltst.u2020mvp.data.rx.EndlessObserver;
import ru.ltst.u2020mvp.ui.ApplicationScope;
import ru.ltst.u2020mvp.ui.gallery.GalleryView;

/**
 * Created by lintonye on 15-02-02.
 */
public class GalleryViewBox extends MirrorAnimatorSandbox {
    private final Context mContext;

    public GalleryViewBox(View root) {
        super(root);
        mContext = root.getContext();
    }


    @Override
    public void enterSandbox() {
        GalleryDatabase database = GalleryViewBoxComponent.Initializer.get(mContext).database();
        Log.d("GBox", "database="+database);
        database.loadGallery(Section.HOT, new EndlessObserver<List<Image>>() {
            @Override
            public void onNext(List<Image> images) {
                Log.d("GBox", "images="+images);
//                List<Image> oimages = images;
//                images = new ArrayList<>();
//                images.add(oimages.get(0));
//                images.add(oimages.get(1));

                GalleryView gallery = (GalleryView) $(R.id.gallery_view).getView();
                gallery.getAdapter().replaceWith(images);
                gallery.setDisplayedChildId(R.id.gallery_grid);
            }
        });

    }

    @Module
    public static class MirrorApplicationModule {
        private final Application mApplication;

        public MirrorApplicationModule(Context context) {
            mApplication = (Application) context.getApplicationContext();
        }

        @Provides
        public Application provideApplication() {
            return mApplication;
        }
    }

    @ApplicationScope
    @Component(modules = {MirrorApplicationModule.class, DebugDataModule.class})
    public interface GalleryViewBoxComponent {
        void inject(GalleryView galleryView);

        GalleryDatabase database();

        final static class Initializer {
            private static GalleryViewBoxComponent sComponent;

            public static GalleryViewBoxComponent get(Context context) {
                if (sComponent == null) {
                    GalleryViewBoxComponent component = Dagger_GalleryViewBox_GalleryViewBoxComponent.builder()
                            .mirrorApplicationModule(new MirrorApplicationModule(context))
                            .build();
                    sComponent = component;
                }
                return sComponent;
            }

            private Initializer() {
            } // No instances.
        }
    }


}
