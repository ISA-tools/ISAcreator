//package org.isatools.isacreator.locationbrowser;
//
//import org.jdesktop.swingx.JXMapKit;
//import org.jdesktop.swingx.JXMapViewer;
//import org.jdesktop.swingx.mapviewer.GeoPosition;
//import org.jdesktop.swingx.mapviewer.Waypoint;
//import org.jdesktop.swingx.mapviewer.WaypointPainter;
//import org.jdesktop.swingx.mapviewer.WaypointRenderer;
//import org.jdesktop.swingx.painter.Painter;
//import org.isatools.isacreator.common.UIHelper;
//import org.isatools.isacreator.effects.borders.RoundedBorder;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.geom.Point2D;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.List;
//
//public class LocationBrowser extends JFrame {
//
//	JXMapKit map;
//
//	public LocationBrowser() {
//
//	}
//
//	private void createGUI() {
//		setTitle("MAP");
//		setLayout(new BorderLayout());
//
//		map = new JXMapKit();
//		map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
//		map.setAddressLocation(new GeoPosition(41.881944, -87.627778));
//		map.setZoomSliderVisible(false);
//		map.setAddressLocationShown(true);
//
//		// customise zooming buttons.
//		map.getZoomInButton().setOpaque(false);
//		map.getZoomInButton().setBackground(UIHelper.GREY_COLOR);
//		map.getZoomInButton().setForeground(UIHelper.LIGHT_GREY_COLOR);
//
//		map.getMiniMap().setBorder(new RoundedBorder(UIHelper.LIGHT_GREY_COLOR, 4));
//
//		addWaypoint();
//		add(map, BorderLayout.CENTER);
//
//		setPreferredSize(new Dimension(400, 400));
//		pack();
//		setVisible(true);
//	}
//
//	public static void main(String[] args) {
//
//		LocationBrowser lb = new LocationBrowser();
//
//		lb.createGUI();
//
//
//	}
//
//	public void addWaypoint() {
//		//create a Set of waypoints
//		Set<Waypoint> waypoints = new HashSet<Waypoint>();
//		waypoints.add(new Waypoint(41.881944, -87.627778));
//		waypoints.add(new Waypoint(40.716667, -74));
//
//		//crate a WaypointPainter to draw the points
//		WaypointPainter painter = new WaypointPainter();
//		painter.setWaypoints(waypoints);
//		painter.setWaypoints(waypoints);
//
//		painter.setRenderer(new WaypointRenderer() {
//			public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp) {
//				g.setColor(Color.RED);
//				g.drawLine(-5, -5, +5, +5);
//				g.drawLine(-5, +5, +5, -5);
//				return true;
//			}
//		});
//		map.getMainMap().setOverlayPainter(painter);
//	}
//
//	public Painter<JXMapViewer> createPolygonOverlay(final List<GeoPosition> region) {
//		return new Painter<JXMapViewer>() {
//			public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
//				g = (Graphics2D) g.create();
//				//convert from viewport to world bitmap
//				Rectangle rect = map.getViewportBounds();
//				g.translate(-rect.x, -rect.y);
//
//				//create a polygon
//				Polygon poly = new Polygon();
//				for(GeoPosition gp : region) {
//					//convert geo to world bitmap pixel
//					Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
//					poly.addPoint((int)pt.getX(),(int)pt.getY());
//				}
//
//				//do the drawing
//				g.setColor(new Color(255,0,0,100));
//				g.fill(poly);
//				g.setColor(Color.RED);
//				g.draw(poly);
//
//				g.dispose();
//			}
//		};
//	}
//
//	private List<GeoPosition> createRegion() {
//		final List<GeoPosition> region = new ArrayList<GeoPosition>();
//		region.add(new GeoPosition(38.266,12.4));
//		region.add(new GeoPosition(38.283,15.65));
//		region.add(new GeoPosition(36.583,15.166));
//		region.add(new GeoPosition(37.616,12.25));
//
//		return region;
//	}
//}