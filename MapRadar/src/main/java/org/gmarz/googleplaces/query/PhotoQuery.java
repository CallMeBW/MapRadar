package org.gmarz.googleplaces.query;

public class PhotoQuery extends SearchQuery {


	public PhotoQuery(String photoRef,int maxW) {
		setRef(photoRef);
        setMaxWidth(maxW);
	}
	//https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRnAAAAtOWKX17fP1dy0dbP3_KmFNis97hQLL3gEJu-_63uXONqTkh-gXGkwngRnfGZ07DCh27Kf2iA1ChlqvRLOEInC5OOJ-G1bSN8GZiwoT5xFi7A7PF430hsqP5jrU1KmoGNyUaPIaV80JdAeK-Yzjv6PhIQZMkw9RoGdjTnalyDuzvX4BoUfmI66-pIheQHz_CzHgrYJFVe9aM&key=AIzaSyBamxP_zAHM93Co50vhony9HCrAIPpW83A
	public void setRef(String photoRef)	{
		mQueryBuilder.addParameter("photoreference", photoRef);
	}
    public void setMaxWidth(int maxWidth)	{
		mQueryBuilder.addParameter("maxwidth", maxWidth+"");
	}
    public void setMaxHeight(int maxHeight)	{
		mQueryBuilder.addParameter("maxheight ", maxHeight+"");
	}

	@Override
	public String getUrl() {
		return "https://maps.googleapis.com/maps/api/place/photo";
	}
}
