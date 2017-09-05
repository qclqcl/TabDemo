package satellite.tle.image;

public class Satinfo {
	
	private int id;
	private String sid;
	private String name;
	private String tle1;
	private String tle2;
	private String namecn;
	private String launch;
	private String launchtime;
	private String weight;
	private Integer valid;
	private String info;
	private String imgurl;
	private String type;
	private String createtime;

	public Satinfo() {
	}

	public Satinfo(String sid, String name, String tle1, String tle2, String namecn, String launch, String launchtime,
			String weight, Integer valid, String info, String imgurl, String type, String createtime) {
		this.sid = sid;
		this.name = name;
		this.tle1 = tle1;
		this.tle2 = tle2;
		this.namecn = namecn;
		this.launch = launch;
		this.launchtime = launchtime;
		this.weight = weight;
		this.valid = valid;
		this.info = info;
		this.imgurl = imgurl;
		this.type = type;
		this.createtime = createtime;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSid() {
		return this.sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTle1() {
		return this.tle1;
	}

	public void setTle1(String tle1) {
		this.tle1 = tle1;
	}

	public String getTle2() {
		return this.tle2;
	}

	public void setTle2(String tle2) {
		this.tle2 = tle2;
	}

	public String getNamecn() {
		return this.namecn;
	}

	public void setNamecn(String namecn) {
		this.namecn = namecn;
	}

	public String getLaunch() {
		return this.launch;
	}

	public void setLaunch(String launch) {
		this.launch = launch;
	}

	public String getLaunchtime() {
		return this.launchtime;
	}

	public void setLaunchtime(String launchtime) {
		this.launchtime = launchtime;
	}

	public String getWeight() {
		return this.weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public Integer getValid() {
		return this.valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getImgurl() {
		return this.imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

}
