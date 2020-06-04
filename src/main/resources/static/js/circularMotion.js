//Initial Setup
var canvas = document.querySelector('canvas');
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

var c = canvas.getContext('2d');

const mouse = {
  x: innerWidth / 2,
  y: innerHeight / 2
};

var colors = [
  '#F8B195',
  '#F67280',
  '#C06C84',
  '#6C5B7B',
  '#355C7D'
];

window.addEventListener('mousemove', function(event) {
  mouse.x = event.clientX;
  mouse.y = event.clientY;
});

window.addEventListener('resize', function() {
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
  init();
});

window.addEventListener('click', function() {
  init();
});

//useful functions
function randomColor(colors) {
  return colors[Math.floor(Math.random() * colors.length)];
}

function randomIntFromRange(min, max) {
  return Math.floor(Math.random() * (max - min + 1) + min);
}

//Objects
function Particle(x, y, radius, color) {
  this.x = x;
  this.y = y;
  this.radius = radius;
  this.color = color;
  this.radians = Math.random() * Math.PI * 2;
  this.velocity = 0.05;
  this.distanceFromCenter = randomIntFromRange(50, 120);
  this.lastMouse = {
    x: x,
    y: y
  };
  //galaxy effect
  // this.distanceFromCenter = {
  //   x: randomIntFromRange(120, 120),
  //   y: randomIntFromRange(120, 120)
  // };
  this.update = () => {
    const lastPoint = {
      x: this.x,
      y: this.y
    };
    mouse
    //move points over time
    this.radians += this.velocity;

    // Drag effect on mouse movement
    this.lastMouse.x += (mouse.x - this.lastMouse.x) * 0.05;
    this.lastMouse.y += (mouse.y - this.lastMouse.y) * 0.05;

    //circular motion
    this.x = this.lastMouse.x + Math.cos(this.radians) * this.distanceFromCenter;
    this.y = this.lastMouse.y + Math.sin(this.radians) * this.distanceFromCenter;

    this.draw(lastPoint);
  };

  this.draw = lastPoint => {
    c.beginPath();
    c.strokeStyle = this.color;
    c.lineWidth = this.radius;
    c.moveTo(lastPoint.x, lastPoint.y);
    c.lineTo(this.x, this.y);
    c.stroke();
    c.closePath();
  };
};

//implementation
let particles;

function init() {
  particles = [];
  console.log('init()');
  for (let i = 0; i < 100; i++) {
    var radius = (Math.random() * 2) + 1;
    var color = randomColor(colors);
    particles.push(new Particle(canvas.width / 2, canvas.height / 2, radius, color));
  }

  console.log(particles);
}

//animation
function animate() {
  console.log('animate()');
  requestAnimationFrame(animate);
  //tracing effect
  c.fillStyle = 'rgba(255,255,255,0.05)';
  c.fillRect(0, 0, canvas.width, canvas.height);

  particles.forEach(particle => {
    particle.update();
  });

  //c.fillText("This is canvas babe!^^", mouse.x, mouse.y);
}


init();
animate();