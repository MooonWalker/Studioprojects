var myHeading = document.querySelector('h1');
var myButton = document.querySelector('button');
var myText = document.querySelector('textarea');

function addTag()
{
	var myName = prompt('Please enter your name.');
	localStorage.setItem('name', myName);
	myHeading.innerHTML = 'Mozilla is cool, ' + myName;
}

if(!localStorage.getItem('name')) 
{
  addTag();
} 
else 
{
  var storedName = localStorage.getItem('name');
  myHeading.innerHTML = 'Mozilla is cool, ' + storedName;
}

//myButton.onClick = myHeading.innerHTML = 'Kaka';