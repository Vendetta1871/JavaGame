#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 outTexCoord;
out float outLight;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform vec3 lightPos;

void main()
{
    outTexCoord = texCoord;

    float ambientStrength = 0.3;
    vec3 lightDir = normalize(lightPos - vec3(modelViewMatrix * vec4(position, 1.0)));
    float diff = max(dot(vertexNormal, lightDir), 0.0);
    outLight = ambientStrength + diff;

    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
}
