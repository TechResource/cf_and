package flightpath.com.inspectionmodule;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Tomasz Szafran ( tomek@appsvisio.com ) on 2016-03-08.
 */
public class InspectionsUtilities {

    public static void createImageLoaderInstance(Context context) {
        if (ImageLoader.getInstance().isInited())
            return;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();

        ImageLoaderConfiguration imageConfiguration = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(imageConfiguration);

    }
}
