package icu.hearme.idphoto.enums

enum class PicSizeType(val code: Int, val value: String, val width: Int, val height: Int) {
    OneInch(1, "1寸", 295,413),
    TwoInch(2, "2寸",413,579),
    ThreeInch(3,"3R/5寸",1050,1500),
    FourInch(4,"4R/6寸",1200,1800),
    FiveInch(5,"5R/7寸",1500,2100),
    DriverLicense(9,"驾照",260, 378);

    fun getAspectRatio(): Float {
        return this.width.toFloat() / this.height.toFloat()
    }
}