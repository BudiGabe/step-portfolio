google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/**
 * Fetch data from server about genres and their votes and display them in a pie chart
 */
function drawChart() {
  fetch('/genre-chart').then(response => response.json())
  .then((genreVotes) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Genre');
    data.addColumn('number', 'Votes');
    Object.keys(genreVotes).forEach((genre) => {
      data.addRow([genre, genreVotes[genre]]);
    });

    const options = {
      'title': 'Most popular genres',
      'width':600,
      'height':500
    };

    const genreChart = new google.visualization.PieChart(
        document.getElementById('chart-container'));
    genreChart.draw(data, options);
  });
}