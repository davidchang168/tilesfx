/*
 * Copyright (c) 2017 by Gerrit Grunwald
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

package eu.hansolo.tilesfx.skins;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.chart.SmoothedChart;
import eu.hansolo.tilesfx.events.SmoothedChartEvent;
import eu.hansolo.tilesfx.events.TileEvent;
import eu.hansolo.tilesfx.events.TileEvent.EventType;
import eu.hansolo.tilesfx.fonts.Fonts;
import eu.hansolo.tilesfx.tools.Helper;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;
import javafx.scene.text.Text;


/**
 * Created by hansolo on 19.12.16.
 */
public class AreaChartTileSkin extends TileSkin {
    private Text                             titleText;
    private SmoothedChart<String, Number>    chart;
    private Axis                             xAxis;
    private Axis                             yAxis;
    private EventHandler<SmoothedChartEvent> chartEventEventHandler;


    // ******************** Constructors **************************************
    public AreaChartTileSkin(final Tile TILE) {
        super(TILE);
    }

    // ******************** Initialization ************************************
    @Override protected void initGraphics() {
        super.initGraphics();

        chartEventEventHandler = e -> tile.fireTileEvent(new TileEvent(EventType.SELECTED_CHART_DATA, new ChartData(e.getValue())));

        titleText = new Text();
        titleText.setFill(tile.getTitleColor());
        Helper.enableNode(titleText, !tile.getTitle().isEmpty());

        xAxis = tile.getXAxis();
        yAxis = tile.getYAxis();

        chart = new SmoothedChart<>(xAxis, yAxis);
        chart.getData().addAll(tile.getSeries());
        chart.setFilled(true);
        chart.setLegendSide(Side.TOP);
        chart.setVerticalZeroLineVisible(false);
        chart.setCreateSymbols(false);
        chart.setSnapToTicks(tile.isSnapToTicks());
        chart.setDataPointsVisible(tile.getDataPointsVisible());

        getPane().getChildren().addAll(titleText, chart);
    }

    @Override protected void registerListeners() {
        super.registerListeners();
        chart.addEventHandler(SmoothedChartEvent.DATA_SELECTED, chartEventEventHandler);
    }


    // ******************** Methods *******************************************
    @Override protected void handleEvents(final String EVENT_TYPE) {
        super.handleEvents(EVENT_TYPE);
        if ("VISIBILITY".equals(EVENT_TYPE)) {
            chart.setDataPointsVisible(tile.getDataPointsVisible());
        }
    }

    @Override public void dispose() {
        chart.removeEventHandler(SmoothedChartEvent.DATA_SELECTED, chartEventEventHandler);
        super.dispose();
    }


    // ******************** Resizing ******************************************
    @Override protected void resizeStaticText() {
        double maxWidth = width - size * 0.1;
        double fontSize = size * textSize.factor;

        titleText.setFont(Fonts.latoRegular(fontSize));
        if (titleText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(titleText, maxWidth, fontSize); }

        switch(tile.getTitleAlignment()) {
            default    :
            case LEFT  : titleText.relocate(size * 0.05, size * 0.05); break;
            case CENTER: titleText.relocate((width - titleText.getLayoutBounds().getWidth()) * 0.5, size * 0.05); break;
            case RIGHT : titleText.relocate(width - (size * 0.05) - titleText.getLayoutBounds().getWidth(), size * 0.05); break;
        }
    }

    @Override protected void resize() {
        super.resize();

        chart.setMinSize(width - size * 0.1, height - size * 0.1);
        chart.setPrefSize(width - size * 0.1, height - size * 0.1);
        chart.setMaxSize(width - size * 0.1, height - size * 0.1);
        chart.setPadding(new Insets(titleText.getLayoutBounds().getHeight() + size * 0.05, 0, 0, 0));
        chart.relocate(size * 0.05, size * 0.05);
    }

    @Override protected void redraw() {
        super.redraw();
        titleText.setText(tile.getTitle());

        resizeStaticText();
        chart.setSelectorStrokeColor(tile.getForegroundColor());
        chart.setSelectorFillColor(tile.getBackgroundColor());

        titleText.setFill(tile.getTitleColor());
    }
}
