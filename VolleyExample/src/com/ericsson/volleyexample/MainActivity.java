package com.ericsson.volleyexample;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView txtDisplay, bracketDisplay;
	String url, webUrl;
	RequestQueue queue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtDisplay = (TextView) findViewById(R.id.txtDisplay);
		bracketDisplay = (TextView) findViewById(R.id.tvBracket);

		queue = Volley.newRequestQueue(this);

		// url =
		// "https://www.googleapis.com/customsearch/v1?key=AIzaSyBmSXUzVZBKQv9FJkTpZXn0dObKgEQOIFU&cx=014099860786446192319:t5mr0xnusiy&q=AndroidDev&alt=json&searchType=image";
		webUrl = "http://www.stff.se/tavling-domare/resultat-tabeller/?scr=table&ftid=47082";

		// addJasonObject(url);
		addString(webUrl);

	}

	private void addJasonObject(String url) {
		// TODO Auto-generated method stub

		txtDisplay.setText(url);

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) { // TODO
						// TODO Auto-generated method stub
						txtDisplay.setText("Response => " + response.toString());
						findViewById(R.id.progressBar1)
								.setVisibility(View.GONE);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) { // TODO
						// TODO Auto-generated method stub
					}
				});

		queue.add(jsObjRequest);

	}

	private void addString(String url) {
		// TODO Auto-generated method stub

		txtDisplay.setText(webUrl);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						String parseResult = parseSanktan(response);
						bracketDisplay.setText(parseResult);
						findViewById(R.id.progressBar1)
								.setVisibility(View.GONE);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});

		queue.add(stringRequest);
	}

	protected String parseSanktan(String response) {
		// TODO Auto-generated method stub
		// Remove everything above the bracket
		String table = "";
		String tableTopDelims = "Hämta widget för tabell";
		String[] tokens1 = response.split(tableTopDelims);

		// Remove everything below the bracket
		String tableBottomDelims = "table";
		String[] tokens2 = tokens1[1].split(tableBottomDelims);

		// Split the table in rows of strings
		String rowDelims = "href";
		String[] tableRow = tokens2[0].split(rowDelims);

		// Get the team, S, V, O, F, GMIM, D and P per row
		// The team is between the first > and the first <
		// The S, V, O, F and GMIM are between <td> and </td>
		// D and P are after > in the tail of GMIM
		String teamDelims = "[><]";
		String[][] team = new String[20][2];
		String columnDelims = "<td>";
		String[] column = new String[6];
		String gameDelims = "</td>";
		String pointDelims = "[>]";
		String[] S = new String[2];
		String[] V = new String[2];
		String[] O = new String[2];
		String[] F = new String[2];
		String[] GMIM = new String[2];
		String[] D = new String[2];
		String[] P = new String[2];

		StringBuilder sb = new StringBuilder();
		int rest;
		for (int i = 1; i < tableRow.length; i++) {
			team[i] = tableRow[i].split(teamDelims);

			// Set the length of the team String to minimum 25
			// I does not align the columns in the TextView and the function is
			// therefore disabled by appending an empty string
			rest = 25 - team[i][1].length();
			sb.setLength(0);
			sb.append(team[i][1]);
			for (int k = 0; k < rest; k++) {
				sb.append("");
			}
			team[i][1] = sb.toString();

			column = tableRow[i].split(columnDelims);
			column[0] = column[0].replaceAll("[\r\n\t]", "");
			if (column[0].endsWith("</a></td>")) {
				S = column[1].split(gameDelims);
				V = column[2].split(gameDelims);
				O = column[3].split(gameDelims);
				F = column[4].split(gameDelims);
				GMIM = column[5].split(gameDelims);

				D = GMIM[1].split(pointDelims);
				P = GMIM[2].split(pointDelims);
			} else {
				S[0] = "Utgått";
				V[0] = O[0] = F[0] = GMIM[0] = D[1] = P[1] = "";
			}

			table += team[i][1] + "\t" + S[0] + "\t" + V[0] + "\t" + O[0]
					+ "\t" + F[0] + "\t" + GMIM[0] + "\t" + D[1] + "\t" + P[1]
					+ "\n";
		}

		return table;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
