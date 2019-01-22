package uk.co.caprica.vlcj.swtdemo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_instance_t;
import uk.co.caprica.vlcj.factory.swt.SwtMediaPlayerFactory;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.linux.LinuxVideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.mac.MacVideoSurfaceAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.swt.CompositeVideoSurface;
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

        Composite videoSurfaceComposite = new Composite(main, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        videoSurfaceComposite.setLayout(gridLayout);
        videoSurfaceComposite.setLayoutData(gridData);
        videoSurfaceComposite.setBackground(black);

        // The only thing that differs from "vanilla" vlcj is to use the SWT factory for access to the SWT Composite
        // video surface - the media player is a normal embedded media player
        SwtMediaPlayerFactory factory = new SwtMediaPlayerFactory();
        EmbeddedMediaPlayer mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();

        CompositeVideoSurface videoSurface = factory.swt().newCompositeVideoSurface(videoSurfaceComposite);
        mediaPlayer.videoSurface().setVideoSurface(videoSurface);

        shell.setLocation(100, 100);
        shell.setSize(800, 450);

        shell.open();

        Media media = factory.media().newMedia(args[0]);
        mediaPlayer.media().set(media);
        mediaPlayer.controls().play();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        mediaPlayer.controls().stop();
        mediaPlayer.release();
        media.release();
        factory.release();

        display.dispose();
    }

}
