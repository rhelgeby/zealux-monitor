package no.helgeby.zealux.chart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChartDatasetGroup {

	public List<String> labels = new ArrayList<>();

	public List<BigDecimal> waterInTemp = new ArrayList<>();
	public List<BigDecimal> waterOutTemp = new ArrayList<>();
	public List<BigDecimal> compressorCurrent = new ArrayList<>();
	public List<BigDecimal> compressorFrequency = new ArrayList<>();
	public List<BigDecimal> eevSteps = new ArrayList<>();
	public List<BigDecimal> fanSpeed = new ArrayList<>();
	public List<BigDecimal> heatingPipeTemp = new ArrayList<>();
	public List<BigDecimal> exhaustTemp = new ArrayList<>();
	public List<BigDecimal> ambientTemp = new ArrayList<>();

	public List<BigDecimal> ah = new ArrayList<>();
	public List<BigDecimal> kwh = new ArrayList<>();

}
