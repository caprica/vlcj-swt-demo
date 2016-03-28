package uk.co.caprica.vlcjswtdemo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_instance_t;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.linux.LinuxVideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.mac.MacVideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.windows.WindowsVideoSurfaceAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

/**
 * The default implementation assumes Linux, changes may be required to fully support other or multiple OS.
 */
public class VlcjSwt {

    public static void main (String[] args) {
        if (args.length != 1) {
            System.err.println("Specify an MRL");
            System.exit(1);
        }

        System.setProperty("sun.awt.xembedserver", "true");

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("vlcj SWT Demo");

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

        shell.setLayout(gridLayout);

        Color black = display.getSystemColor(SWT.COLOR_BLACK);

        Composite main = new Composite(shell, SWT.NONE);
        main.setLayout(gridLayout);
        main.setLayoutData(gridData);
        main.setBackground(black);

        Composite videoSurface = new Composite(main, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        videoSurface.setLayout(gridLayout);
        videoSurface.setLayoutData(gridData);
        videoSurface.setBackground(black);

        LibVlc libvlc = LibVlc.INSTANCE;
        libvlc_instance_t instance = libvlc.libvlc_new(0, null);

        SwtEmbeddedMediaPlayer mediaPlayer = new SwtEmbeddedMediaPlayer(libvlc, instance);
        mediaPlayer.setVideoSurface(new CompositeVideoSurface(videoSurface, getVideoSurfaceAdapter()));

        shell.setLocation(100, 100);
        shell.setSize(800, 450);

        shell.open();

        mediaPlayer.playMedia(args[0]);

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        display.dispose();
    }

    private static VideoSurfaceAdapter getVideoSurfaceAdapter() {
        VideoSurfaceAdapter videoSurfaceAdapter;
        if(RuntimeUtil.isNix()) {
            videoSurfaceAdapter = new LinuxVideoSurfaceAdapter();
        }
        else if(RuntimeUtil.isWindows()) {
            videoSurfaceAdapter = new WindowsVideoSurfaceAdapter();
        }
        else if(RuntimeUtil.isMac()) {
            videoSurfaceAdapter = new MacVideoSurfaceAdapter();
        }
        else {
            throw new RuntimeException("Unable to create a media player - failed to detect a supported operating system");
        }
        return videoSurfaceAdapter;
    }
}
