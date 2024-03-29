class PoolStatistics {
    async loadData(filePath) {
        let response = await fetch(filePath);
        if (!response.ok) {
            throw "Error when fetching data.";
        }
        this.dataSource = await response.json();
    }

    buildChart(canvasId) {
        if (!this.dataSource) {
            throw "Data not loaded.";
        }

        const canvasContext = document.getElementById(canvasId).getContext('2d');
        const data = this.dataSource[canvasId];
        // First index is dash length, second is length of gaps between dashes.
        const dashSegments = [6, 3];

        const skipped = (ctx, value) => ctx.p0.skip || ctx.p1.skip ? value : undefined;

        const chartData = {
            labels: data.labels,
            datasets: [{
                label: 'Water In',
                data: data.waterInTemp,
                backgroundColor: [
                    'rgba(0, 112, 192, 0.2)'
                ],
                borderColor: [
                    'rgba(0, 112, 192, 1)'
                ],
                borderWidth: 2,
                pointRadius: 0,
                hidden: false,
                cubicInterpolationMode: 'monotone',
                segment: {
                    borderColor: ctx => skipped(ctx, 'rgba(0, 112, 192, 0.5)'),
                    borderDash: ctx => skipped(ctx, dashSegments),
                  },
                spanGaps: true
            },
            {
                label: 'Water Out',
                data: data.waterOutTemp,
                backgroundColor: [
                    'rgba(255, 64, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255, 64, 64, 1)'
                ],
                borderWidth: 2,
                pointRadius: 0,
                hidden: true,
                cubicInterpolationMode: 'monotone',
                segment: {
                    borderColor: ctx => skipped(ctx, 'rgba(255, 64, 64, 0.5)'),
                    borderDash: ctx => skipped(ctx, dashSegments),
                  },
                spanGaps: true
            },
            {
                label: 'Compressor Current',
                data: data.compressorCurrent,
                backgroundColor: [
                    'rgba(102, 102, 102, 0.2)'
                ],
                borderColor: [
                    'rgba(102, 102, 102, 1)'
                ],
                borderWidth: 2,
                pointRadius: 0,
                hidden: false,
                yAxisID: 'y2',
                cubicInterpolationMode: 'monotone',
                segment: {
                    borderColor: ctx => skipped(ctx, 'rgba(102, 102, 102, 0.5)'),
                    borderDash: ctx => skipped(ctx, dashSegments),
                  },
                spanGaps: true
            },
            {
                label: 'Exhaust Temp',
                data: data.exhaustTemp,
                backgroundColor: [
                    'rgba(237, 125, 49, 0.2)'
                ],
                borderColor: [
                    'rgba(237, 125, 49, 1)'
                ],
                borderWidth: 2,
                pointRadius: 0,
                hidden: true,
                cubicInterpolationMode: 'monotone',
                segment: {
                    borderColor: ctx => skipped(ctx, 'rgba(237, 125, 49, 0.5)'),
                    borderDash: ctx => skipped(ctx, dashSegments),
                  },
                spanGaps: true
            },
            {
                label: 'Ambient Temp',
                data: data.ambientTemp,
                backgroundColor: [
                    'rgba(84, 130, 53, 0.2)'
                ],
                borderColor: [
                    'rgba(84, 130, 53, 1)'
                ],
                borderWidth: 2,
                pointRadius: 0,
                hidden: true,
                cubicInterpolationMode: 'monotone',
                segment: {
                    borderColor: ctx => skipped(ctx, 'rgba(84, 130, 53, 0.5)'),
                    borderDash: ctx => skipped(ctx, dashSegments),
                  },
                spanGaps: true
            }
            ]
        };

        const chart = new Chart(canvasContext, {
            type: 'line',
            data: chartData,
            options: {
                maintainAspectRatio: false,
                scales: {
                    y: {
                        ticks: {
                            stepSize: 0.1
                        },
                        stack: 'stack1',
                        stackWeight: 2,
                        stacked: false
                    },
                    y2: {
                        stack: 'stack1',
                        stackWeight: 1,
                        stacked: true
                    }
    
                },
                interaction: {
                    mode: 'index',
                    intersect: false
                }
            }
        });
    }
}

async function init() {
    const statistics = new PoolStatistics();
    await statistics.loadData('data.json');
    statistics.buildChart('pastTwoDays');
    statistics.buildChart('pastWeek');
    statistics.buildChart('pastMonth');
    statistics.buildChart('allTimeDaily');
}

init();
