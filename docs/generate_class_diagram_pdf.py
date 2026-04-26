from reportlab.lib import colors
from reportlab.lib.pagesizes import A3, landscape
from reportlab.pdfbase.pdfmetrics import stringWidth
from reportlab.pdfgen import canvas


PAGE_WIDTH, PAGE_HEIGHT = landscape(A3)
MARGIN = 28


BOXES = {
    "Propietario": {
        "x": 35,
        "y": 510,
        "w": 180,
        "title": "Propietario",
        "attrs": ["id : Long", "nombre : String", "apellidos : String", "telefono : String", "email : String"],
    },
    "Mascota": {
        "x": 270,
        "y": 500,
        "w": 190,
        "title": "Mascota",
        "attrs": ["id : Long", "nombre : String", "especie : String", "raza : String", "fechaNacimiento : LocalDate"],
    },
    "Usuario": {
        "x": 35,
        "y": 265,
        "w": 180,
        "title": "Usuario",
        "attrs": ["id : Long", "nombreCompleto : String", "username : String", "password : String", "rol : RolUsuario"],
    },
    "Veterinario": {
        "x": 530,
        "y": 515,
        "w": 195,
        "title": "Veterinario",
        "attrs": ["id : Long", "nombre : String", "apellidos : String", "especialidad : String", "email : String"],
    },
    "Cita": {
        "x": 770,
        "y": 500,
        "w": 170,
        "title": "Cita",
        "attrs": ["id : Long", "fechaHora : LocalDateTime", "motivo : String", "estado : EstadoCita"],
    },
    "HistorialMedico": {
        "x": 510,
        "y": 270,
        "w": 215,
        "title": "HistorialMedico",
        "attrs": ["id : Long", "fecha : LocalDate", "descripcion : String", "diagnostico : String"],
    },
    "Vacunacion": {
        "x": 270,
        "y": 270,
        "w": 190,
        "title": "Vacunacion",
        "attrs": ["id : Long", "tipoVacuna : String", "fechaAplicada : LocalDate", "proximaDosis : LocalDate"],
    },
    "Tratamiento": {
        "x": 780,
        "y": 285,
        "w": 180,
        "title": "Tratamiento",
        "attrs": ["id : Long", "nombre : String", "dosis : String", "frecuencia : String", "estado : EstadoTratamiento"],
    },
    "DocumentoClinico": {
        "x": 780,
        "y": 95,
        "w": 180,
        "title": "DocumentoClinico",
        "attrs": ["id : Long", "titulo : String", "tipoDocumento : TipoDocumentoClinico", "nombreArchivo : String"],
    },
    "Factura": {
        "x": 1010,
        "y": 500,
        "w": 170,
        "title": "Factura",
        "attrs": ["id : Long", "numero : String", "fechaEmision : LocalDate", "total : BigDecimal", "estado : EstadoFactura"],
    },
}


RELATIONS = [
    ("Propietario", "Mascota", "1", "0..*", "tiene"),
    ("Propietario", "Usuario", "1", "0..*", "usa"),
    ("Mascota", "Vacunacion", "1", "0..*", "recibe"),
    ("Mascota", "HistorialMedico", "1", "0..*", "genera"),
    ("Mascota", "Cita", "1", "0..*", "registra"),
    ("Mascota", "Factura", "1", "0..*", "produce"),
    ("Veterinario", "Cita", "1", "0..*", "atiende"),
    ("Veterinario", "HistorialMedico", "1", "0..*", "crea"),
    ("HistorialMedico", "Tratamiento", "1", "0..*", "incluye"),
    ("HistorialMedico", "DocumentoClinico", "1", "0..*", "adjunta"),
    ("Cita", "Factura", "0..1", "0..*", "origina"),
]


def box_height(attrs):
    return 32 + 20 * len(attrs) + 12


def draw_box(c, spec):
    x = spec["x"]
    y = spec["y"]
    w = spec["w"]
    h = box_height(spec["attrs"])
    c.setStrokeColor(colors.HexColor("#0f172a"))
    c.setFillColor(colors.white)
    c.roundRect(x, y, w, h, 8, stroke=1, fill=1)
    c.setFillColor(colors.HexColor("#e0f2fe"))
    c.roundRect(x, y + h - 30, w, 30, 8, stroke=0, fill=1)
    c.setStrokeColor(colors.HexColor("#0f172a"))
    c.line(x, y + h - 30, x + w, y + h - 30)
    c.setFont("Helvetica-Bold", 12)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.drawString(x + 10, y + h - 20, spec["title"])
    c.setFont("Helvetica", 9)
    ty = y + h - 45
    for attr in spec["attrs"]:
        c.drawString(x + 10, ty, attr)
        ty -= 18
    spec["h"] = h


def mid_right(spec):
    return spec["x"] + spec["w"], spec["y"] + spec["h"] / 2


def mid_left(spec):
    return spec["x"], spec["y"] + spec["h"] / 2


def mid_top(spec):
    return spec["x"] + spec["w"] / 2, spec["y"] + spec["h"]


def mid_bottom(spec):
    return spec["x"] + spec["w"] / 2, spec["y"]


def pick_points(a, b):
    if a["x"] + a["w"] < b["x"]:
        return mid_right(a), mid_left(b)
    if b["x"] + b["w"] < a["x"]:
        return mid_left(a), mid_right(b)
    if a["y"] > b["y"]:
        return mid_bottom(a), mid_top(b)
    return mid_top(a), mid_bottom(b)


def draw_arrow_head(c, x1, y1, x2, y2):
    import math

    angle = math.atan2(y2 - y1, x2 - x1)
    size = 8
    left = angle + math.pi - 0.35
    right = angle + math.pi + 0.35
    c.line(x2, y2, x2 + size * math.cos(left), y2 + size * math.sin(left))
    c.line(x2, y2, x2 + size * math.cos(right), y2 + size * math.sin(right))


def draw_relation(c, start_spec, end_spec, start_card, end_card, label):
    (x1, y1), (x2, y2) = pick_points(start_spec, end_spec)
    c.setStrokeColor(colors.HexColor("#334155"))
    c.setLineWidth(1)
    if abs(y1 - y2) < 8 or abs(x1 - x2) < 8:
        c.line(x1, y1, x2, y2)
    else:
        mx = (x1 + x2) / 2
        c.line(x1, y1, mx, y1)
        c.line(mx, y1, mx, y2)
        c.line(mx, y2, x2, y2)
    draw_arrow_head(c, x1, y1, x2, y2)
    c.setFont("Helvetica", 8)
    c.setFillColor(colors.HexColor("#334155"))
    c.drawString(x1 + 4, y1 + 6, start_card)
    c.drawString(x2 - 18, y2 + 6, end_card)
    lx = (x1 + x2) / 2 - stringWidth(label, "Helvetica", 8) / 2
    ly = (y1 + y2) / 2 + 10
    c.setFillColor(colors.white)
    c.rect(lx - 3, ly - 2, stringWidth(label, "Helvetica", 8) + 6, 12, stroke=0, fill=1)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.drawString(lx, ly, label)


def create_pdf(output_path):
    c = canvas.Canvas(output_path, pagesize=landscape(A3))
    c.setTitle("Diagrama de Clases - Clinica Veterinaria")

    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont("Helvetica-Bold", 22)
    c.drawString(MARGIN, PAGE_HEIGHT - 35, "Diagrama de Clases - Clinica Veterinaria")
    c.setFont("Helvetica", 11)
    c.setFillColor(colors.HexColor("#475569"))
    c.drawString(MARGIN, PAGE_HEIGHT - 52, "Version simplificada para entrega academica")

    for spec in BOXES.values():
        draw_box(c, spec)

    for start, end, scard, ecard, label in RELATIONS:
        draw_relation(c, BOXES[start], BOXES[end], scard, ecard, label)

    c.setFont("Helvetica", 9)
    c.setFillColor(colors.HexColor("#64748b"))
    c.drawRightString(PAGE_WIDTH - MARGIN, 18, "Generado automaticamente desde el proyecto")
    c.save()


if __name__ == "__main__":
    create_pdf("docs/diagrama-clases-entrega.pdf")
